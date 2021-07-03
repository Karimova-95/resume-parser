package com.example.resumeparser.service;

import com.example.resumeparser.entity.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String hhApi = "https://api.hh.ru/resumes";

    int responseCode = 0;
    private HttpURLConnection getConnection(String id) throws IOException {
        //        String url = hhApi+ "/" + id;
        String url = "https://42ae178b-dc1f-40ed-a589-6349b8385425.mock.pstmn.io/resumes/0123456789abcdef";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        return con;
    }
    public GoodResponse getResumeById(String id) throws IOException {

        ResumeMin resumeMin = null;
        try {
            StringBuffer response = getResponse(id);
            JSONObject myresponse = new JSONObject(response.toString());
            JSONObject photo_object = new JSONObject(myresponse.getJSONObject("photo").toString());
            resumeMin = new ResumeMin(myresponse.getString("id"),
                    photo_object.getString("small"),
                    myresponse.getString("last_name") + " " +
                            myresponse.getString("first_name") + " " + myresponse.getString("middle_name"));
            System.out.println(resumeMin);
        } catch (Exception ex) {
            System.out.println("Ошибка");
        }
        GoodResponse goodResponse = new GoodResponse(responseCode, resumeMin);
        return  goodResponse;
    }

    public BadResponse incorrectURL() {
        BadResponse badResponse = new BadResponse(0, "Resume not found");
        return  badResponse;
    }

    public boolean checkID(String id) throws IOException {
        HttpURLConnection con = getConnection(id);
        if (con.getResponseCode() == 200) {
            responseCode = 1;
        }
        return (responseCode == 1) ? true : false;
    }

    public boolean withName(String format) {
        return format.equals("withName") ? true : false;
    }



    private StringBuffer getResponse(String id) throws IOException {
        HttpURLConnection con = getConnection(id);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response;
    }

    public GoodResponse getFullResume(String id) {

        FullResumeWithName resume = new FullResumeWithName();
        try {
            StringBuffer response = getResponse(id);
            JSONObject myresponse = new JSONObject(response.toString());
            JSONObject gender_object = new JSONObject(myresponse.getJSONObject("gender").toString());
            JSONObject area_object = new JSONObject(myresponse.getJSONObject("area").toString());
            JSONObject photo_object = new JSONObject(myresponse.getJSONObject("photo").toString());
            resume.setId(myresponse.getString("id"));
            resume.setName(myresponse.getString("last_name") + " " +
                    "" + myresponse.getString("first_name") + " " +
                    "" + myresponse.getString("middle_name"));
            resume.setAge(myresponse.getInt("age"));
            resume.setBirth_date(myresponse.getString("birth_date"));
            resume.setGender(Gender.fromString(gender_object.getString("name")));
            resume.setArea(area_object.getString("name"));
            resume.setTitle(myresponse.getString("title"));

            JSONArray contactArray = myresponse.getJSONArray("contact");
            List<Contact> contactList = new ArrayList<>();
            for (int i = 0; i < contactArray.length(); i++) {
                JSONObject contactType = new JSONObject(contactArray.getJSONObject(i).getJSONObject("type").toString());
                String type = contactType.getString("id");
                switch (type) {
                    case "cell":
                        Cell c = new Cell();
                        c.setType(type);
                        JSONObject contactValue = new JSONObject(contactArray.getJSONObject(i).getJSONObject("value").toString());
                        c.setValue(contactValue.getString("formatted"));
                        contactList.add(c);
                        break;
                    case "email":
                        Email e = new Email();
                        e.setType(type);
                        e.setValue(contactArray.getJSONObject(i).getString("value"));
                        contactList.add(e);
                        break;
                }
            }
            JSONArray siteArray = myresponse.getJSONArray("site");
            for (int i = 0; i < siteArray.length(); i++) {
                JSONObject syteType = new JSONObject(siteArray.getJSONObject(i).getJSONObject("type").toString());
                Site s = new Site();
                s.setType(syteType.getString("id"));
                s.setValue(siteArray.getJSONObject(i).getString("url"));
                contactList.add(s);
            }
            resume.setContacts(contactList.toArray());

            Photo photo = new Photo();
            photo.setSmall(photo_object.getString("small"));
            photo.setMedium(photo_object.getString("medium"));
            resume.setPhoto(photo);

            JSONObject educationArray = myresponse.getJSONObject("education");
            JSONArray additional = educationArray.getJSONArray("additional");
            Education[] education = new Education[additional.length()];
            for (int i = 0; i < additional.length(); i++) {
                Education e = new Education();
                e.setYear(additional.getJSONObject(i).getInt("year"));
                e.setResult(additional.getJSONObject(i).getString("result"));
                e.setOrganization(additional.getJSONObject(i).getString("organization"));
                e.setName(additional.getJSONObject(i).getString("name"));
                education[i] = e;
            }
            resume.setEducation(education);


            JSONArray languageArray = myresponse.getJSONArray("language");
            Language[] language = new Language[languageArray.length()];
            for (int i = 0; i < languageArray.length(); i++) {
                Language l = new Language();
                JSONObject level = languageArray.getJSONObject(i).getJSONObject("level");
                l.setLevel(level.getString("id"));
                l.setId(languageArray.getJSONObject(i).getString("id"));
                l.setName(languageArray.getJSONObject(i).getString("name"));
                language[i] = l;
            }
            resume.setLanguage(language);

            JSONArray experienceArray = myresponse.getJSONArray("experience");
            Experience[] experience = new Experience[experienceArray.length()];
            for (int i = 0; i < experienceArray.length(); i++) {
                Experience ex = new Experience();
                ex.setCompany(experienceArray.getJSONObject(i). getString("company"));
                ex.setPosition(experienceArray.getJSONObject(i). getString("position"));
                ex.setStart(experienceArray.getJSONObject(i). getString("start"));
                ex.setEnd(experienceArray.getJSONObject(i). getString("end"));
                ex.setDescription(experienceArray.getJSONObject(i). getString("description"));
                JSONObject area_object_exper = experienceArray.getJSONObject(i).getJSONObject("area");
                ex.setArea(area_object_exper.getString("name"));

                experience[i] = ex;
            }
            resume.setExperience(experience);

            JSONArray skillSetArray = myresponse.getJSONArray("skill_set");
            List<String> skillsList = new ArrayList<>();
            for (int i = 0; i < skillSetArray.length(); i++) {
                skillsList.add(skillSetArray.getString(i));
            }
            resume.setSkill_set(skillsList.toArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(resume.toString());
        return  new GoodResponse(1, resume);
    }

    public GoodResponse getFullResumeWithoutName(String id) {
        FullResumeWithoutName resume = new FullResumeWithoutName();
        try {
            StringBuffer response = getResponse(id);
            JSONObject myresponse = new JSONObject(response.toString());
            JSONObject gender_object = new JSONObject(myresponse.getJSONObject("gender").toString());
            JSONObject area_object = new JSONObject(myresponse.getJSONObject("area").toString());
            JSONObject photo_object = new JSONObject(myresponse.getJSONObject("photo").toString());
            resume.setId(myresponse.getString("id"));
            resume.setAge(myresponse.getInt("age"));
            resume.setBirth_date(myresponse.getString("birth_date"));
            resume.setGender(Gender.fromString(gender_object.getString("name")));
            resume.setArea(area_object.getString("name"));
            resume.setTitle(myresponse.getString("title"));

            JSONArray contactArray = myresponse.getJSONArray("contact");
            List<Contact> contactList = new ArrayList<>();
            for (int i = 0; i < contactArray.length(); i++) {
                JSONObject contactType = new JSONObject(contactArray.getJSONObject(i).getJSONObject("type").toString());
                String type = contactType.getString("id");
                switch (type) {
                    case "cell":
                        Cell c = new Cell();
                        c.setType(type);
                        JSONObject contactValue = new JSONObject(contactArray.getJSONObject(i).getJSONObject("value").toString());
                        c.setValue(contactValue.getString("formatted"));
                        contactList.add(c);
                        break;
                    case "email":
                        Email e = new Email();
                        e.setType(type);
                        e.setValue(contactArray.getJSONObject(i).getString("value"));
                        contactList.add(e);
                        break;
                }
            }
            JSONArray siteArray = myresponse.getJSONArray("site");
            for (int i = 0; i < siteArray.length(); i++) {
                JSONObject syteType = new JSONObject(siteArray.getJSONObject(i).getJSONObject("type").toString());
                Site s = new Site();
                s.setType(syteType.getString("id"));
                s.setValue(siteArray.getJSONObject(i).getString("url"));
                contactList.add(s);
            }
            resume.setContacts(contactList.toArray());

            Photo photo = new Photo();
            photo.setSmall(photo_object.getString("small"));
            photo.setMedium(photo_object.getString("medium"));
            resume.setPhoto(photo);

            JSONObject educationArray = myresponse.getJSONObject("education");
            JSONArray additional = educationArray.getJSONArray("additional");
            Education[] education = new Education[additional.length()];
            for (int i = 0; i < additional.length(); i++) {
                Education e = new Education();
                e.setYear(additional.getJSONObject(i).getInt("year"));
                e.setResult(additional.getJSONObject(i).getString("result"));
                e.setOrganization(additional.getJSONObject(i).getString("organization"));
                e.setName(additional.getJSONObject(i).getString("name"));
                education[i] = e;
            }
            resume.setEducation(education);


            JSONArray languageArray = myresponse.getJSONArray("language");
            Language[] language = new Language[languageArray.length()];
            for (int i = 0; i < languageArray.length(); i++) {
                Language l = new Language();
                JSONObject level = languageArray.getJSONObject(i).getJSONObject("level");
                l.setLevel(level.getString("id"));
                l.setId(languageArray.getJSONObject(i).getString("id"));
                l.setName(languageArray.getJSONObject(i).getString("name"));
                language[i] = l;
            }
            resume.setLanguage(language);

            JSONArray experienceArray = myresponse.getJSONArray("experience");
            Experience[] experience = new Experience[experienceArray.length()];
            for (int i = 0; i < experienceArray.length(); i++) {
                Experience ex = new Experience();
                ex.setCompany(experienceArray.getJSONObject(i). getString("company"));
                ex.setPosition(experienceArray.getJSONObject(i). getString("position"));
                ex.setStart(experienceArray.getJSONObject(i). getString("start"));
                ex.setEnd(experienceArray.getJSONObject(i). getString("end"));
                ex.setDescription(experienceArray.getJSONObject(i). getString("description"));
                JSONObject area_object_exper = experienceArray.getJSONObject(i).getJSONObject("area");
                ex.setArea(area_object_exper.getString("name"));

                experience[i] = ex;
            }
            resume.setExperience(experience);

            JSONArray skillSetArray = myresponse.getJSONArray("skill_set");
            List<String> skillsList = new ArrayList<>();
            for (int i = 0; i < skillSetArray.length(); i++) {
                skillsList.add(skillSetArray.getString(i));
            }
            resume.setSkill_set(skillsList.toArray());

            System.out.println(resume);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(resume.toString());
        return  new GoodResponse(1, resume);
    }
}
