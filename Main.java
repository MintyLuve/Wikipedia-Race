// Java Program to Extract Content from a HTML document

// Importing input/output java libraries
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        String START_PAGE = "Banana";
        String END_PAGE = "Quantum_mechanics";

        bogoFind(START_PAGE, END_PAGE);

    }

    static String getHTML(String page){
        String websiteLink = "https://en.wikipedia.org/wiki/"+page;

        // Tries a request to get the page info
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(websiteLink))
                .header("User-Agent", "Wikipedia_School_Project")
                .GET()
                .build();
            // Gets the link and returns it
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response.body().toString();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    // Filters out the link suffixes
    static ArrayList<String> filterLinks(String HTMLtext, String startPage){
        String originalHTML = HTMLtext;
        ArrayList<String> links = new ArrayList<>();
        String linkStub = "/wiki/";
        String linkSuffix = "";
        String c = "";
        int i = 0;

        while (HTMLtext.length() > 0){
            // If there are no links left, break
            if (!HTMLtext.contains(linkStub)){ break; }
                        
            // Find the beginning of the next link stub
            i = HTMLtext.indexOf(linkStub) +linkStub.length() -1;
            // Get the first char
            c = String.valueOf(HTMLtext.charAt(i+1));

            // Get the full link suffix
            while (!c.equals("\"")){
                linkSuffix += c;
                i++;
                c = String.valueOf(HTMLtext.charAt(i+1));
            }


            // Sorts to have real links
            if (!linkSuffix.contains(":") && !linkSuffix.contains("Main_Page") && !linkSuffix.startsWith("%") && !linkSuffix.contains("</a>") && !linkSuffix.contains("#") && !linkSuffix.contains("?") && !links.contains(linkSuffix) && !startPage.equals(linkSuffix)) {
                links.add(linkSuffix);
            }

            // Shortens the text to after the link stub
            HTMLtext = HTMLtext.substring(i, HTMLtext.length());

            // Resets the strings
            c = "";
            linkSuffix = "";
        }

        // If there are no links on the page, add the start page
        if (links.size() == 0){  
            System.out.println("ERROR: links.size() = 0");
        }
        return links;
    }

    static String bogoFind(String startPage, String endPage){
        String pageText = getHTML(startPage);
        String currentPage = startPage;
        ArrayList<String> links = filterLinks(pageText, startPage);
        int pagesVisited = 0;
        int pageIndex = 0;
        Random r = new Random();
        int size = links.size();

        // While the current page isn't the page we want
        while (!currentPage.equals(endPage)){
            // Get a random index from the list of links
            if (size > 0) { pageIndex = r.nextInt(0, size); }
            else { pageIndex = 0; }
            
            // Get the page and text from the index
            currentPage = links.get(pageIndex);
            pageText = getHTML(currentPage);

            // Filter a new set of links
            links = filterLinks(pageText, startPage);
            size = links.size();

            // Increment the amount of pages visited for the sysout
            pagesVisited++;
            System.out.println(pagesVisited + " clicks - Visited " + currentPage +".");
        }
        
        // Return the current page if we found the end page
        return currentPage;
    }

}