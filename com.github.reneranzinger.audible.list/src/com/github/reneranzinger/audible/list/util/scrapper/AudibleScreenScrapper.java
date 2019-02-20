package com.github.reneranzinger.audible.list.util.scrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.reneranzinger.audible.list.persist.om.AudibleCategory;
import com.github.reneranzinger.audible.list.persist.om.Book;
import com.github.reneranzinger.audible.list.persist.om.Person;
import com.github.reneranzinger.audible.list.persist.om.Score;
import com.github.reneranzinger.audible.list.persist.om.Serie;

public class AudibleScreenScrapper
{
    private JSONObject m_jsonAudiobook = null;
    private JSONObject m_jsonBreadCrumb = null;
    private JSONObject m_jsonProduct = null;
    private String m_baseUrl = null;

    public Book scrapBookPage(String a_url)
            throws IOException, ParseException, ScreenScapperExcpetion
    {
        // initialization
        this.m_baseUrl = a_url.substring(0, a_url.indexOf("/", 9));
        this.m_jsonAudiobook = null;
        this.m_jsonBreadCrumb = null;
        this.m_jsonProduct = null;

        // download the web page and sanitize the string
        Document t_doc = this.downloadSingleBook(a_url);

        // find the canonical URL and check if its this URL
        String t_canonicalURL = this.getCanonicalUrl(t_doc);
        if (!a_url.equalsIgnoreCase(t_canonicalURL))
        {
            // reload the information from the canonical URL
            t_doc = this.downloadSingleBook(t_canonicalURL);
        }
        Elements t_elementsScript = t_doc.select("script[type=application/ld+json]");
        for (Element t_element : t_elementsScript)
        {
            // get the content of the tag and make sure it contains the key
            // phrase
            String t_json = t_element.html();
            if (t_json.contains("\"Audiobook\""))
            {
                // parse the json and extract the book and bread crumbs
                JSONParser t_parser = new JSONParser();
                Object t_jsonResult = t_parser.parse(t_json);

                this.extractSingleAudiobook(t_jsonResult);
            }
            else if (t_json.contains("\"Product\""))
            {
                // parse the json and extract the product JSON
                JSONParser t_parser = new JSONParser();
                Object t_jsonResult = t_parser.parse(t_json);
                this.extractProduct(t_jsonResult);
            }
        }
        if (this.m_jsonAudiobook == null || this.m_jsonBreadCrumb == null
                || this.m_jsonProduct == null)
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Unable to find audiobook, product or breadcrumb list.");
        }
        return this.extractSingleAudiobookInformation(this.m_jsonAudiobook, this.m_jsonBreadCrumb,
                this.m_jsonProduct, t_doc);
    }

    private Document downloadSingleBook(String a_url)
            throws UnsupportedEncodingException, IOException
    {
        // Document t_doc = Jsoup.connect(a_url).timeout(0).get();
        // we can not use the Jsoup.connect method. It will follow the location
        // and cookie based redirect. We need something naive here.
        String t_page = new String(WebUtil.getWebPage(a_url, 2), "UTF-8");
        Document t_doc = Jsoup.parse(t_page);
        // PrintWriter out = new PrintWriter("filename.txt");
        // out.println(t_doc.html());
        // out.close();
        return t_doc;
    }

    private void extractProduct(Object a_jsonResult) throws ScreenScapperExcpetion
    {
        // is it an array?
        if (a_jsonResult instanceof JSONArray)
        {
            JSONArray t_array = (JSONArray) a_jsonResult;
            // check all objects in the array
            for (Object t_object : t_array)
            {
                if (t_object instanceof JSONObject)
                {
                    // an object, check for the type field
                    JSONObject t_resultObject = (JSONObject) t_object;
                    String t_type = (String) t_resultObject.get("@type");
                    if (t_type != null)
                    {
                        if (t_type.equals("Product"))
                        {
                            this.m_jsonProduct = t_resultObject;
                        }
                    }
                }
            }
        }
        else
        {
            throw new ScreenScapperExcpetion("JSON format error: Product section is not an array.");
        }
    }

    private Book extractSingleAudiobookInformation(JSONObject a_jsonAudiobook,
            JSONObject a_jsonBreadCrumb, JSONObject a_jsonProduct, Document a_doc)
            throws ScreenScapperExcpetion
    {
        Book t_book = new Book();
        String t_stringJsonPart = this.getString(a_jsonAudiobook, "name");
        if (t_stringJsonPart == null)
        {
            throw new ScreenScapperExcpetion("JSON format error: Unable to find book title.");
        }
        t_book.setTitle(t_stringJsonPart);
        t_stringJsonPart = this.getString(a_jsonAudiobook, "image");
        if (t_stringJsonPart == null)
        {
            throw new ScreenScapperExcpetion("JSON format error: Unable to find book image.");
        }
        t_book.setImageUrl(t_stringJsonPart);
        t_stringJsonPart = this.getString(a_jsonAudiobook, "description");
        if (t_stringJsonPart == null)
        {
            throw new ScreenScapperExcpetion("JSON format error: Unable to find book description.");
        }
        t_book.setDescription(t_stringJsonPart);
        t_book.setUrl(this.getCanonicalUrl(a_doc));
        t_book.setSeries(this.getSeries(a_doc));
        t_book.setReleaseDate(this.getDate(a_jsonAudiobook, "datePublished"));
        t_book.setDurationMin(this.getDuration(a_jsonAudiobook, "duration"));
        t_book.setCategories(this.getCategories(a_jsonBreadCrumb));
        List<Person> t_personList = this.getPersons(a_jsonAudiobook, "author");
        if (t_personList.size() == 0)
        {
            throw new ScreenScapperExcpetion("JSON format error: Author list is empty.");
        }
        t_book.setAuthor(t_personList);
        t_personList = this.getPersons(a_jsonAudiobook, "readBy");
        if (t_personList.size() == 0)
        {
            throw new ScreenScapperExcpetion("JSON format error: Reader list is empty.");
        }
        t_book.setReader(t_personList);
        t_book.setCurrentScore(this.getScore(a_jsonAudiobook, "aggregateRating"));
        t_stringJsonPart = this.getString(a_jsonProduct, "productID");
        if (t_stringJsonPart == null)
        {
            throw new ScreenScapperExcpetion("JSON format error: Unable to find product ID.");
        }
        t_book.setProductId(t_stringJsonPart);
        return t_book;
    }

    private String getCanonicalUrl(Document a_doc) throws ScreenScapperExcpetion
    {
        String t_url = null;
        Elements t_elementsLink = a_doc.select("link[rel=canonical]");
        if (t_elementsLink.size() != 1)
        {
            throw new ScreenScapperExcpetion("HTML format error: Unable to find canonical URL.");
        }
        for (Element t_elementLink : t_elementsLink)
        {
            t_url = t_elementLink.attr("href");
            if (t_url == null)
            {
                throw new ScreenScapperExcpetion(
                        "HTML format error: Unable to find URL in the canonical link.");
            }
        }
        return t_url;
    }

    private List<Serie> getSeries(Document a_doc) throws ScreenScapperExcpetion
    {
        List<Serie> t_resultList = new ArrayList<Serie>();
        Elements t_elementsLi = a_doc.select("li.seriesLabel");
        if (t_elementsLi.size() > 2)
        {
            throw new ScreenScapperExcpetion(
                    "HTML format error: more than one 'seriesLabel' elements found.");
        }
        for (Element t_elementLi : t_elementsLi)
        {
            Elements t_elementsA = t_elementLi.select("a");
            if (t_elementsA.size() == 0)
            {
                throw new ScreenScapperExcpetion("HTML format error: no link found for serie.");
            }
            for (Element t_elementA : t_elementsA)
            {
                String t_name = t_elementA.text();
                if (t_name == null || t_name.trim().length() == 0)
                {
                    throw new ScreenScapperExcpetion("HTML format error: no series name found.");
                }
                String t_url = t_elementA.attr("href");
                if (t_name == null || t_name.trim().length() == 0)
                {
                    throw new ScreenScapperExcpetion("HTML format error: no series URL found.");
                }
                t_url = this.cleanSeriesURL(t_url);
                Serie t_serie = new Serie();
                t_serie.setName(t_name);
                t_serie.setUrl(t_url);
                t_resultList.add(t_serie);
            }
        }
        return t_resultList;
    }

    private String cleanSeriesURL(String a_url)
    {
        String t_url = a_url;
        int t_position = a_url.indexOf("&");
        if (t_position != -1)
        {
            t_url = t_url.substring(0, t_position);
        }
        if (t_url.startsWith("/"))
        {
            t_url = this.m_baseUrl + t_url;
        }
        return t_url;
    }

    private Score getScore(JSONObject a_jsonAudiobook, String a_key) throws ScreenScapperExcpetion
    {
        Object t_object = a_jsonAudiobook.get(a_key);
        if (t_object == null)
        {
            return null;
        }
        if (!(t_object instanceof JSONObject))
        {
            throw new ScreenScapperExcpetion("JSON format error: Rating is not an object.");
        }
        JSONObject t_jsonRating = (JSONObject) t_object;
        String t_string = this.getString(t_jsonRating, "ratingCount");
        if (t_string == null)
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Rating object does not have a ratingCount.");
        }
        Integer t_countValue = null;
        try
        {
            t_countValue = Integer.parseInt(t_string);
        }
        catch (Exception e)
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Rating object ratingCount is not a number.", e);
        }
        t_string = this.getString(t_jsonRating, "ratingValue");
        if (t_string == null)
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Rating object does not have a ratingValue.");
        }
        Double t_ratingValue = null;
        try
        {
            t_ratingValue = Double.parseDouble(t_string);
        }
        catch (Exception e)
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Rating object ratingValue is not a number.", e);
        }
        if (t_countValue > 0)
        {
            Score t_score = new Score();
            t_score.setScore(t_ratingValue);
            t_score.setVoters(t_countValue);
            t_score.setDate(new Date());
            return t_score;
        }
        return null;
    }

    private List<AudibleCategory> getCategories(JSONObject a_jsonBreadCrumb)
            throws ScreenScapperExcpetion
    {
        List<AudibleCategory> t_categories = new ArrayList<AudibleCategory>();
        Integer t_positionHighest = 0;
        String t_name = null;
        JSONArray t_jsonItemList = this.getArray(a_jsonBreadCrumb, "itemListElement");
        if (t_jsonItemList == null)
        {
            throw new ScreenScapperExcpetion("JSON format error: Unable to find category list.");
        }
        for (Object t_object : t_jsonItemList)
        {
            if (!(t_object instanceof JSONObject))
            {
                throw new ScreenScapperExcpetion(
                        "JSON format error: category list element is not an object.");
            }
            JSONObject t_jsonCategory = (JSONObject) t_object;
            Integer t_position = this.getPosition(t_jsonCategory, "position");
            if (t_position > t_positionHighest)
            {
                t_position = t_positionHighest;
                t_name = this.getCategoryName(t_jsonCategory);
            }
        }
        if (t_name != null)
        {
            AudibleCategory t_category = new AudibleCategory();
            t_category.setName(t_name);
            t_categories.add(t_category);

        }
        return t_categories;
    }

    private String getCategoryName(JSONObject a_jsonCategory) throws ScreenScapperExcpetion
    {
        Object t_object = a_jsonCategory.get("item");
        if (t_object == null)
        {
            throw new ScreenScapperExcpetion("JSON format error: No item found in category.");
        }
        if (!(t_object instanceof JSONObject))
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Item in category is not an object.");
        }
        JSONObject t_jsonObjectItem = (JSONObject) t_object;
        String t_name = this.getString(t_jsonObjectItem, "name");
        if (t_name == null)
        {
            throw new ScreenScapperExcpetion("JSON format error: No name found in category item.");
        }
        return t_name;
    }

    private Integer getPosition(JSONObject a_jsonCategory, String a_key)
            throws ScreenScapperExcpetion
    {
        Object t_object = a_jsonCategory.get(a_key);
        if (t_object == null)
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Unable to find category position.");
        }
        if (t_object instanceof Long)
        {
            return ((Long) t_object).intValue();
        }
        else
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Value for category position is not a integer.");
        }
    }

    private List<Person> getPersons(JSONObject a_jsonAudiobook, String a_key)
            throws ScreenScapperExcpetion
    {
        List<Person> t_personList = new ArrayList<Person>();
        JSONArray t_array = this.getArray(a_jsonAudiobook, a_key);
        if (t_array == null)
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Unable to find person list: " + a_key);
        }
        for (Object t_object : t_array)
        {
            if (t_object instanceof JSONObject)
            {
                JSONObject t_jsonPerson = (JSONObject) t_object;
                String t_name = this.getString(t_jsonPerson, "name");
                if (t_name == null)
                {
                    throw new ScreenScapperExcpetion(
                            "JSON format error: Person is missing the name attribute in " + a_key);

                }
                Person t_person = new Person();
                t_person.setName(t_name);
                t_personList.add(t_person);
            }
            else
            {
                throw new ScreenScapperExcpetion(
                        "JSON format error: Error in person list format for: " + a_key);

            }
        }
        return t_personList;
    }

    private JSONArray getArray(JSONObject a_jsonAudiobook, String a_key)
            throws ScreenScapperExcpetion
    {
        Object t_stringObject = a_jsonAudiobook.get(a_key);
        if (t_stringObject == null)
        {
            return null;
        }
        if (t_stringObject instanceof JSONArray)
        {
            return (JSONArray) t_stringObject;
        }
        else
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Value for " + a_key + " is not an array.");
        }
    }

    private Integer getDuration(JSONObject a_jsonAudiobook, String a_key)
            throws ScreenScapperExcpetion
    {
        String t_string = this.getString(a_jsonAudiobook, a_key);
        if (t_string == null)
        {
            throw new ScreenScapperExcpetion("JSON format error: Unable to find duration.");
        }
        t_string = t_string.replaceAll("PT", "");
        t_string = t_string.substring(0, t_string.length() - 1);
        String[] t_part = t_string.split("H");
        if (t_part.length == 2)
        {
            try
            {
                Integer t_hours = Integer.parseInt(t_part[0]);
                Integer t_minutes = Integer.parseInt(t_part[1]);
                return t_minutes + (t_hours * 60);
            }
            catch (Exception e)
            {
                throw new ScreenScapperExcpetion(
                        "JSON format error: Number format for duration is incorrect.", e);
            }
        }
        else if (t_part.length == 1)
        {
            try
            {
                Integer t_hours = Integer.parseInt(t_part[0]);
                return (t_hours * 60);
            }
            catch (Exception e)
            {
                throw new ScreenScapperExcpetion(
                        "JSON format error: Number format for duration is incorrect.", e);
            }
        }
        else
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Format for duration is incorrect.");
        }
    }

    private Date getDate(JSONObject a_jsonAudiobook, String a_key) throws ScreenScapperExcpetion
    {
        String t_string = this.getString(a_jsonAudiobook, a_key);
        if (t_string == null)
        {
            throw new ScreenScapperExcpetion("JSON format error: Unable to find release date.");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date t_date = formatter.parse(t_string);
            return t_date;
        }
        catch (java.text.ParseException e)
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Value for " + a_key + " is not a date: " + t_string, e);
        }
    }

    private String getString(JSONObject a_jsonAudiobook, String a_key) throws ScreenScapperExcpetion
    {
        Object t_stringObject = a_jsonAudiobook.get(a_key);
        if (t_stringObject == null)
        {
            return null;
        }
        if (t_stringObject instanceof String)
        {
            return (String) t_stringObject;
        }
        else
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Value for " + a_key + " is not a string.");
        }
    }

    private void extractSingleAudiobook(Object a_jsonResult) throws ScreenScapperExcpetion
    {
        // is it an array?
        if (a_jsonResult instanceof JSONArray)
        {
            JSONArray t_array = (JSONArray) a_jsonResult;
            // check all objects in the array
            for (Object t_object : t_array)
            {
                if (t_object instanceof JSONObject)
                {
                    // an object, check for the type field
                    JSONObject t_resultObject = (JSONObject) t_object;
                    String t_type = (String) t_resultObject.get("@type");
                    if (t_type != null)
                    {
                        if (t_type.equals("BreadcrumbList"))
                        {
                            this.m_jsonBreadCrumb = t_resultObject;
                        }
                        else if (t_type.equals("Audiobook"))
                        {
                            this.m_jsonAudiobook = t_resultObject;
                        }
                    }
                }
            }
        }
        else
        {
            throw new ScreenScapperExcpetion(
                    "JSON format error: Audiobook section is not an array.");
        }
    }

}
