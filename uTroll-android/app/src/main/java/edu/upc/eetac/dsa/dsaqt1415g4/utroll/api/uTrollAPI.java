package edu.upc.eetac.dsa.dsaqt1415g4.utroll.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class uTrollAPI {
    private final static String TAG = uTrollAPI.class.getName();
    private static uTrollAPI instance = null;
    private URL url;

    private uTrollRootAPI rootAPI = null;

    private uTrollAPI(Context context) throws IOException, AppException {
        super();

        AssetManager assetManager = context.getAssets();
        Properties config = new Properties();
        config.load(assetManager.open("config.properties")); //Carga el asset creado "config.properties"
        String urlHome = config.getProperty("uTroll.home"); //Obtener la propiedad por su nombre
        url = new URL(urlHome);

        Log.d("LINKS", url.toString());
        getRootAPI();
    }

    //Es un Singleton
    public final static uTrollAPI getInstance(Context context) throws AppException {
        if (instance == null)
            try {
                instance = new uTrollAPI(context);
            } catch (IOException e) {
                throw new AppException(
                        "Can't load configuration file");
            }
        return instance;
    }

    private void getRootAPI() throws AppException {
        Log.d(TAG, "getRootAPI()");
        rootAPI = new uTrollRootAPI(); //Instancia el model que pide la respuesta a "/"
        HttpURLConnection urlConnection = null; //cnx HTTP
        try {
            urlConnection = (HttpURLConnection) url.openConnection(); //Abrir cnx
            urlConnection.setRequestMethod("GET"); //Indicar método a usar
            urlConnection.setDoInput(true); //Declaras que vas a leer la respuesta
            urlConnection.connect(); //Hacer la petición
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to uTroll API Web Service");
        }

        BufferedReader reader; //Leer la respuesta
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line); //Guardar la respuesta en un StringBuilder (es un JSON)
            }

            JSONObject jsonObject = new JSONObject(sb.toString()); //Se procesa el JSON de respuesta
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, rootAPI.getLinks());
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from uTroll API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing uTroll Root API");
        }

    }

    public CommentCollection getComments() throws AppException {
        Log.d(TAG, "getComments()");
        CommentCollection comments = new CommentCollection(); //Modelo de la colección

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("comments").getTarget()).openConnection(); //Cnx URL Contra el ROOT + el target del atributo "comments"
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to uTroll API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, comments.getLinks());

            JSONArray jsonComments = jsonObject.getJSONArray("comments");
            for (int i = 0; i < jsonComments.length(); i++) {
                Comment comment = new Comment();
                JSONObject jsonComment = jsonComments.getJSONObject(i);

                comment.setCommentid(jsonComment.getInt("commentid"));
                comment.setContent(jsonComment.getString("content"));
                comment.setCreation_timestamp(jsonComment.getLong("creation_timestamp"));
                comment.setCreator(jsonComment.getString("creator"));
                comment.setDislikes(jsonComment.getInt("dislikes"));
                comment.setGroupid(jsonComment.getInt("groupid"));
                comment.setLast_modified(jsonComment.getLong("last_modified"));
                comment.setLikes(jsonComment.getInt("likes"));
                comment.setUsername(jsonComment.getString("username"));

                jsonLinks = jsonComment.getJSONArray("links");
                parseLinks(jsonLinks, comment.getLinks());
                comments.getComments().add(comment);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from uTroll API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing uTroll Root API");
        }

        return comments;
    }

    private Map<String, Comment> commentsCache = new HashMap<String, Comment>();

    public Comment getComment(String urlComment) throws AppException {
        Comment comment = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlComment);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            comment = commentsCache.get(urlComment);
            String eTag = (comment == null) ? null : comment.getETag();
            if (eTag != null)
                urlConnection.setRequestProperty("If-None-Match", eTag);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) { //Si el comment no se ha modificado, se devuelve el de la URL
                Log.d(TAG, "CACHE");
                return commentsCache.get(urlComment);
            }
            Log.d(TAG, "NOT IN CACHE"); //Si sí que se ha modificado, debemos obtenerlo
            comment = new Comment();
            eTag = urlConnection.getHeaderField("ETag");
            comment.setETag(eTag);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonComment = new JSONObject(sb.toString());

            comment.setCommentid(jsonComment.getInt("commentid"));
            comment.setContent(jsonComment.getString("content"));
            comment.setCreation_timestamp(jsonComment.getLong("creation_timestamp"));
            comment.setCreator(jsonComment.getString("creator"));
            comment.setDislikes(jsonComment.getInt("dislikes"));
            comment.setGroupid(jsonComment.getInt("groupid"));
            comment.setLast_modified(jsonComment.getLong("last_modified"));
            comment.setLikes(jsonComment.getInt("likes"));
            comment.setUsername(jsonComment.getString("username"));

            JSONArray jsonLinks = jsonComment.getJSONArray("links");
            parseLinks(jsonLinks, comment.getLinks());

            commentsCache.put(urlComment, comment); //Esta línea se pone al final por si hay error antes
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Bad comment url");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception when getting the comment");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception parsing response");
        }

        return comment;
    }

    public GroupCollection getGroups() throws AppException {
        Log.d(TAG, "getGroups()");
        GroupCollection groups = new GroupCollection(); //Modelo de la colección

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("groups").getTarget()).openConnection(); //Cnx URL Contra el ROOT + el target del atributo "groups"
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to uTroll API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, groups.getLinks());

            JSONArray jsonGroups = jsonObject.getJSONArray("groups");
            for (int i = 0; i < jsonGroups.length(); i++) {
                Group group = new Group();
                JSONObject jsonGroup = jsonGroups.getJSONObject(i);

                group.setGroupid(jsonGroup.getInt("groupid"));
                group.setCreator(jsonGroup.getString("creator"));
                group.setCreationTimestamp(jsonGroup.getLong("creationTimestamp"));
                group.setEndingTimestamp(jsonGroup.getLong("endingTimestamp"));
                group.setGroupname(jsonGroup.getString("groupname"));
                group.setPrice(jsonGroup.getInt("price"));
                group.setState(jsonGroup.getString("state"));

                jsonLinks = jsonGroup.getJSONArray("links");
                parseLinks(jsonLinks, group.getLinks());
                groups.getGroups().add(group);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from uTroll API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing uTroll Root API");
        }

        return groups;
    }

    public void joinGroup(String urlGroup, String mediaType) throws AppException {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlGroup);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonUser = new JSONObject(sb.toString());

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error parsing response");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error getting response");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

    }

    public Boolean checkLogin(String username, String password) throws AppException {
        Log.d(TAG, "checkLogin()");
        Boolean loginOK = false;
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonUser = createJsonUser(user);
            URL urlPostUsers = new URL(rootAPI.getLinks().get("login").getTarget());
            urlConnection = (HttpURLConnection) urlPostUsers.openConnection();
            String mediaType = rootAPI.getLinks().get("login").getParameters().get("type");
            urlConnection.setRequestProperty("Accept",
                    mediaType);
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonUser.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonUser = new JSONObject(sb.toString());

            user.setGroupid(jsonUser.getInt("groupid"));
            user.setLoginSuccessful(jsonUser.getBoolean("loginSuccessful"));
            loginOK = user.isLoginSuccessful();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error parsing response");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error getting response");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return loginOK;
    }

    // Crear JSON de un Usuario
    private JSONObject createJsonUser(User user) throws JSONException {
        JSONObject jsonUser = new JSONObject();
        jsonUser.put("username", user.getUsername());
        jsonUser.put("password", user.getPassword());
        jsonUser.put("name", user.getName());
        jsonUser.put("email", user.getEmail());
        jsonUser.put("age", user.getAge());
        jsonUser.put("points", user.getPoints());
        jsonUser.put("points_max", user.getPoints_max());
        jsonUser.put("groupid", user.getGroupid());
        jsonUser.put("isTroll", user.isTroll());

        return jsonUser;
    }

    //Le pasamos un Array y un Mapa donde vamos a guardar los links
    private void parseLinks(JSONArray jsonLinks, Map<String, Link> map)
            throws AppException, JSONException {
        for (int i = 0; i < jsonLinks.length(); i++) {
            Link link = null;
            try {
                link = SimpleLinkHeaderParser
                        .parseLink(jsonLinks.getString(i));
            } catch (Exception e) {
                throw new AppException(e.getMessage());
            }
            String rel = link.getParameters().get("rel");
            String rels[] = rel.split("\\s"); //La rel del HATEOAS puede tener varios parámetros, ej: rel="create collection" (separadas por espacio)
            for (String s : rels) //En el mapa se guardan las rels y los titles
                map.put(s, link);
        }
    }
}