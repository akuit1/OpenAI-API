import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

public class a {

    public static String chatGPT(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "sk-proj-vwbmsENTIDMqJzBnSJlOT3BlbkFJxXGGcCcNcoRJWrCOg3Xc";
        String model = "gpt-3.5-turbo-0125";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // The request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}], \"max_tokens\": 50}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // calls the method to extract the message.
            return extractMessageandTokensFromJSONResponse(response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractMessageandTokensFromJSONResponse(String response) {

        int start1 = response.indexOf("content")+ 11;
        int end1 = response.indexOf("\"", start1);

        String a = response.substring(start1, end1);

        int start2 = response.indexOf("total_tokens") + 14;
        int end2 = response.indexOf("}", start2);

        String b = response.substring(start2, end2);

        String myEol = System.getProperty("line.separator");

        return "Response: " + a + myEol + "Tokens used:" + b;

    }

    public static void insertIntoDatabase(String prompt, String response, int tokensUsed) {
        String url = "jdbc:mysql://localhost:3306/chat_logs";
        String username = "java";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO messages (prompt, response, tokens_used) VALUES (?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, prompt);
                statement.setString(2, response);
                statement.setInt(3, tokensUsed);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting into database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        String prompt = "who was the longest standing king in england";
        String response = chatGPT(prompt);
        System.out.println(response);


        int tokensUsed = 50;
        insertIntoDatabase(prompt, response, tokensUsed);

    }
}