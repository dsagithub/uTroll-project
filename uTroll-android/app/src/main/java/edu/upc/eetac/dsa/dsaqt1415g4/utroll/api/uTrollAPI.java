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

    public CommentCollection getPrevNextComments(String urlPN) throws AppException {
        Log.d(TAG, "getPrevNextComments()");
        CommentCollection comments = new CommentCollection(); //Modelo de la colección

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlPN);
            urlConnection = (HttpURLConnection) url.openConnection();
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

    public Comment LikeDislikeComment(String urlLikeDislike, String mediaType, String urlComment) throws AppException {
        Comment comment = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlLikeDislike);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

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

    public Comment createComment(String content, String username) throws AppException {
        Log.d(TAG, "createComment()");
        Comment comment = new Comment();
        comment.setContent(content);
        if (username != null)
            comment.setUsername(username);
        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonComment = createJsonComment(comment);
            URL urlPostComments = new URL(rootAPI.getLinks().get("post-comment")
                    .getTarget());
            urlConnection = (HttpURLConnection) urlPostComments.openConnection();
            String mediaType = rootAPI.getLinks().get("post-comment").getParameters().get("type"); //Esta línea no estaba en el gist
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonComment.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonComment = new JSONObject(sb.toString());

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

        return comment;
    }

    private JSONObject createJsonComment(Comment comment) throws JSONException {
        JSONObject jsonComment = new JSONObject();
        jsonComment.put("content", comment.getContent());
        if (comment.getUsername() != null)
            jsonComment.put("username", comment.getUsername());

        return jsonComment;
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
                //group.setEndingTimestamp(jsonGroup.getLong("endingTimestamp"));
                group.setGroupname(jsonGroup.getString("groupname"));
                group.setPrice(jsonGroup.getInt("price"));
                group.setState(jsonGroup.getString("state"));

                jsonLinks = jsonGroup.getJSONArray("links");
                parseLinks(jsonLinks, group.getLinks());

                if (group.getGroupid() != 0) //El grupo 0 (vacío) no se debe mostrar
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

    public Group getGroupByGroupid(String urlGroup) throws AppException {
        Log.d(TAG, "getGroupByGroupid()");
        Group group = new Group();

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlGroup);
            urlConnection = (HttpURLConnection) url.openConnection();
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
            JSONObject jsonGroup = new JSONObject(sb.toString());

            group.setGroupid(jsonGroup.getInt("groupid"));
            group.setCreator(jsonGroup.getString("creator"));
            group.setCreationTimestamp(jsonGroup.getLong("creationTimestamp"));
            //group.setEndingTimestamp(jsonGroup.getLong("endingTimestamp"));
            group.setGroupname(jsonGroup.getString("groupname"));
            group.setPrice(jsonGroup.getInt("price"));
            group.setState(jsonGroup.getString("state"));

            JSONArray jsonLinks = jsonGroup.getJSONArray("links");
            parseLinks(jsonLinks, group.getLinks());
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from uTroll API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing uTroll Root API");
        }

        return group;
    }

    public Group createGroup(String groupname, int price, String endingTimestamp, String closingTimestamp) throws AppException {
        Log.d(TAG, "createGroup()");
        Group group = new Group();
        group.setGroupname(groupname);
        group.setPrice(price);
        group.setEndingTimestamp(endingTimestamp);
        group.setClosingTimestamp(closingTimestamp);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonGroup = createJsonGroup(group);
            URL urlPostGroups = new URL(rootAPI.getLinks().get("create-group")
                    .getTarget());
            urlConnection = (HttpURLConnection) urlPostGroups.openConnection();
            String mediaType = rootAPI.getLinks().get("create-group").getParameters().get("type"); //Esta línea no estaba en el gist
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonGroup.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonGroup = new JSONObject(sb.toString());

            group.setGroupid(jsonGroup.getInt("groupid"));
            group.setCreator(jsonGroup.getString("creator"));
            group.setCreationTimestamp(jsonGroup.getLong("creationTimestamp"));
            //group.setEndingTimestamp(jsonGroup.getLong("endingTimestamp"));
            group.setGroupname(jsonGroup.getString("groupname"));
            group.setPrice(jsonGroup.getInt("price"));
            group.setState(jsonGroup.getString("state"));

            JSONArray jsonLinks = jsonGroup.getJSONArray("links");
            parseLinks(jsonLinks, group.getLinks());
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

        return group;
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

    public void changeGroupState(String urlGroup, String mediaType, String state) throws AppException {
        HttpURLConnection urlConnection = null;
        Group group = new Group();
        group.setState(state);

        try {
            JSONObject jsonGroup = createJsonGroup(group);
            URL url = new URL(urlGroup);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept",
                    mediaType);
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            PrintWriter writer = new PrintWriter(urlConnection.getOutputStream());
            writer.println(jsonGroup.toString());
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

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

    // Crear JSON de un Grupo
    private JSONObject createJsonGroup(Group group) throws JSONException {
        JSONObject jsonGroup = new JSONObject();
        jsonGroup.put("groupid", group.getGroupid());
        jsonGroup.put("creator", group.getCreator());
        jsonGroup.put("groupname", group.getGroupname());
        jsonGroup.put("price", group.getPrice());
        jsonGroup.put("state", group.getState());
        jsonGroup.put("endingTimestamp", group.getEndingTimestamp());
        jsonGroup.put("closingTimestamp", group.getClosingTimestamp());

        return jsonGroup;
    }

    private Map<String, User> usersCache = new HashMap<String, User>();

    public User getUser(String urlUser) throws AppException {
        User user = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlUser);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            user = usersCache.get(urlUser);
            String eTag = (user == null) ? null : user.getETag();
            if (eTag != null)
                urlConnection.setRequestProperty("If-None-Match", eTag);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) { //Si el user no se ha modificado, se devuelve el de la URL
                Log.d(TAG, "CACHE");
                return usersCache.get(urlUser);
            }
            Log.d(TAG, "NOT IN CACHE"); //Si sí que se ha modificado, debemos obtenerlo
            user = new User();
            eTag = urlConnection.getHeaderField("ETag");
            user.setETag(eTag);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonUser = new JSONObject(sb.toString());

            user.setUsername(jsonUser.getString("username"));
            user.setEmail(jsonUser.getString("email"));
            user.setName(jsonUser.getString("name"));
            user.setAge(jsonUser.getInt("age"));
            user.setGroupid(jsonUser.getInt("groupid"));
            user.setPoints(jsonUser.getInt("points"));
            user.setPoints_max(jsonUser.getInt("points_max"));
            user.setTroll(jsonUser.getBoolean("troll"));
            user.setVotedBy(jsonUser.getInt("votedBy"));
            user.setVote(jsonUser.getString("vote"));

            JSONArray jsonLinks = jsonUser.getJSONArray("links");
            parseLinks(jsonLinks, user.getLinks());

            usersCache.put(urlUser, user); //Esta línea se pone al final por si hay error antes
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Bad user url");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception when getting the user");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception parsing response");
        }

        return user;
    }

    public void voteTroll(String username) throws AppException {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("vote").getTarget() + "/vote/" + username).openConnection();
            String mediaType = rootAPI.getLinks().get("vote").getParameters().get("type");
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

    public FriendList getFriend(String urlFriend) throws AppException {
        HttpURLConnection urlConnection = null;
        FriendList friend = new FriendList();
        friend.setState("none");
        friend.setRequest(false);
        try {
            URL url = new URL(urlFriend);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Libreria API Web Service");
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

            JSONObject jsonFriend = new JSONObject(sb.toString());

            friend.setState(jsonFriend.getString("state"));
            friend.setRequest(jsonFriend.getBoolean("request"));
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Bad user url");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception when getting the user");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception parsing response");
        }

        return friend;
    }

    public UserCollection getPendingFriends() throws AppException {
        Log.d(TAG, "getPendingFriends()");
        UserCollection users = new UserCollection(); //Modelo de la colección

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("pending-friends").getTarget()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Libreria API Web Service");
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
            parseLinks(jsonLinks, users.getLinks());

            JSONArray jsonUsers = jsonObject.getJSONArray("users");
            for (int i = 0; i < jsonUsers.length(); i++) {
                User user = new User();
                JSONObject jsonUser = jsonUsers.getJSONObject(i);

                user.setUsername(jsonUser.getString("username"));

                jsonLinks = jsonUser.getJSONArray("links");
                parseLinks(jsonLinks, user.getLinks());
                users.getUsers().add(user);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from uTroll API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing uTroll Root API");
        }

        return users;
    }

    public void addFriend(String username) throws AppException {
        Log.d(TAG, "addFriend()");
        FriendList friend = new FriendList();
        friend.setFriend1(username);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonFriend = createJsonFriend(friend);
            URL urladdFriend = new URL(rootAPI.getLinks().get("friend").getTarget() + "/addFriend/" + username);
            urlConnection = (HttpURLConnection) urladdFriend.openConnection();
            String mediaType = rootAPI.getLinks().get("friend").getParameters().get("type"); //Esta línea no estaba en el gist
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonFriend.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
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

    public void acceptFriend(String username) throws AppException {
        Log.d(TAG, "acceptFriend()");
        FriendList friend = new FriendList();
        friend.setFriend1(username);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonFriend = createJsonFriend(friend);
            URL urladdFriend = new URL(rootAPI.getLinks().get("friend").getTarget() + "/acceptFriend/" + username);
            urlConnection = (HttpURLConnection) urladdFriend.openConnection();
            String mediaType = rootAPI.getLinks().get("friend").getParameters().get("type"); //Esta línea no estaba en el gist
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonFriend.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
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

    // Crear JSON de un FriendList
    private JSONObject createJsonFriend(FriendList friend) throws JSONException {
        JSONObject jsonFriend = new JSONObject();
        jsonFriend.put("friend1", friend.getFriend1());
        jsonFriend.put("friend2", friend.getFriend2());
        jsonFriend.put("state", friend.getState());
        jsonFriend.put("request", friend.getRequest());
        jsonFriend.put("friendshipd", friend.getFriendshipid());

        return jsonFriend;
    }

    public UserCollection getUsersByUsername(String username) throws AppException {
        Log.d(TAG, "getUsersByUsername()");
        UserCollection users = new UserCollection(); //Modelo de la colección

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("users").getTarget() + "?username=" + username).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Libreria API Web Service");
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
            parseLinks(jsonLinks, users.getLinks());

            JSONArray jsonUsers = jsonObject.getJSONArray("users");
            for (int i = 0; i < jsonUsers.length(); i++) {
                User user = new User();
                JSONObject jsonUser = jsonUsers.getJSONObject(i);

                user.setUsername(jsonUser.getString("username"));
                user.setEmail(jsonUser.getString("email"));
                user.setName(jsonUser.getString("name"));
                user.setAge(jsonUser.getInt("age"));
                user.setGroupid(jsonUser.getInt("groupid"));
                user.setPoints(jsonUser.getInt("points"));
                user.setPoints_max(jsonUser.getInt("points_max"));
                user.setTroll(jsonUser.getBoolean("troll"));
                user.setVotedBy(jsonUser.getInt("votedBy"));
                user.setVote(jsonUser.getString("vote"));

                jsonLinks = jsonUser.getJSONArray("links");
                parseLinks(jsonLinks, user.getLinks());
                users.getUsers().add(user);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from uTroll API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing uTroll Root API");
        }

        return users;
    }

    public UserCollection getUsersInGroup(String urlGetUsers) throws AppException {
        Log.d(TAG, "getUsersInGroup()");
        UserCollection users = new UserCollection(); //Modelo de la colección

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlGetUsers);
            urlConnection = (HttpURLConnection) url.openConnection();
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
            parseLinks(jsonLinks, users.getLinks());

            JSONArray jsonUsers = jsonObject.getJSONArray("users");
            for (int i = 0; i < jsonUsers.length(); i++) {
                User user = new User();
                JSONObject jsonUser = jsonUsers.getJSONObject(i);

                user.setUsername(jsonUser.getString("username"));
                user.setEmail(jsonUser.getString("email"));
                user.setName(jsonUser.getString("name"));
                user.setAge(jsonUser.getInt("age"));
                user.setGroupid(jsonUser.getInt("groupid"));
                user.setPoints(jsonUser.getInt("points"));
                user.setPoints_max(jsonUser.getInt("points_max"));
                user.setTroll(jsonUser.getBoolean("troll"));
                user.setVotedBy(jsonUser.getInt("votedBy"));
                user.setVote(jsonUser.getString("vote"));

                jsonLinks = jsonUser.getJSONArray("links");
                parseLinks(jsonLinks, user.getLinks());
                users.getUsers().add(user);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from uTroll API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing uTroll Root API");
        }

        return users;
    }

    public User checkLogin(String username, String password) throws AppException {
        Log.d(TAG, "checkLogin()");
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
            user.setTroll(jsonUser.getBoolean("troll"));

            JSONArray jsonLinks = jsonUser.getJSONArray("links");
            parseLinks(jsonLinks, user.getLinks());
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

        return user;
    }

    public User createUser(String username, String name, int age, String email, String password) throws AppException {
        Log.d(TAG, "createUser()");
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(password);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonUser = createJsonUser(user);
            URL urlPostUsers = new URL(rootAPI.getLinks().get("create-user")
                    .getTarget());
            urlConnection = (HttpURLConnection) urlPostUsers.openConnection();
            String mediaType = rootAPI.getLinks().get("create-user").getParameters().get("type"); //Esta línea no estaba en el gist
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
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

            user.setUsername(jsonUser.getString("username"));
            user.setPoints(Integer.parseInt(jsonUser.getString("points")));

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

        return user;
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
        jsonUser.put("troll", user.isTroll());

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