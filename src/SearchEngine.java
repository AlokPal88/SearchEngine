/*
 * CSCE 5200 - Information Retrieval and Web Search, Spring 2017
 * Final Project - A Web Search Engine
 * Author: Alok Pal 
 * */
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

import java.io.IOException;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.HttpStatusException;
import javax.swing.JScrollPane;
import java.awt.Color;

public class SearchEngine {
	static final int MAX_PAGES_TO_SEARCH = 500;
	private static Set<String> pagesVisited = new HashSet<String>();
	private static Queue<String> pagesToVisit = new LinkedList<String>();
	private static String userQuery;
	private static WordCounter w = new WordCounter();
	private static HashMap<String, String> webDocs = new HashMap<String, String>();
	private static ArrayList<String> returnedLinks;
	private static ListIterator<String> it;

	private static JFrame frmWebCrawlerDeveloped;
	private static JTextField searchQuery;
	private static JTextArea searchResult;
	private static String[] result = {"Lets","see", "if","this","works!"};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SearchEngine window = new SearchEngine();
					window.frmWebCrawlerDeveloped.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SearchEngine() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWebCrawlerDeveloped = new JFrame();
		frmWebCrawlerDeveloped.setForeground(Color.BLUE);
		frmWebCrawlerDeveloped.setFont(new Font("Dialog", Font.BOLD, 12));
		frmWebCrawlerDeveloped.setBounds(100, 100, 924, 497);
		frmWebCrawlerDeveloped.setTitle("CSCE 5200          Web Search Engine          Alok Pal");
		frmWebCrawlerDeveloped.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWebCrawlerDeveloped.getContentPane().setLayout(null);
		
		searchQuery = new JTextField();
		searchQuery.setFont(new Font("Tahoma", Font.BOLD, 25));
		searchQuery.setBounds(0, 0, 902, 60);
		frmWebCrawlerDeveloped.getContentPane().add(searchQuery);
		searchQuery.setColumns(10);
		
		JButton search = new JButton("Search");
		search.setFont(new Font("Tahoma", Font.BOLD, 20));
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchResult.setText(null);
				startCrawling(searchQuery.getText());
			}
		});
		search.setBounds(390, 64, 136, 46);
		frmWebCrawlerDeveloped.getContentPane().add(search);
		
		//result = {"Lets","see", "if","this","works!"};
		
		searchResult = new JTextArea();
		searchResult.setWrapStyleWord(true);
		searchResult.setFont(new Font("Rockwell", Font.PLAIN, 25));
		searchResult.setBounds(0, 115, 902, 240);
		frmWebCrawlerDeveloped.getContentPane().add(searchResult);
		searchResult.setColumns(10);
		
		JButton prev = new JButton("Next");
		prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fetchNextFiveResults();
			}
		});
		prev.setBounds(617, 371, 115, 40);
		frmWebCrawlerDeveloped.getContentPane().add(prev);
		
		JButton next = new JButton("Previous");
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fetchPreviousFiveResults();
			}
		});
		next.setBounds(161, 371, 115, 40);
		frmWebCrawlerDeveloped.getContentPane().add(next);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(207, 222, 279, 111);
		frmWebCrawlerDeveloped.getContentPane().add(scrollPane);
	}
	public static void displaySearchResult(){
		for(int i=0; i < result.length; i++)
		{
			searchResult.setText(searchResult.getText() + result[i] + "\n");
		}
	}
	
	//New code integrated here
	public static void fetchNextFiveResults()
	{
		searchResult.setText(null);
		int breakpoint = 0;
		
		while (it.hasNext())
		{
			if (breakpoint == 5)
				break;
			String docID = it.next();
			searchResult.setText(searchResult.getText() + docID + "\n");
			breakpoint ++;
		}
	}
	
	public static void fetchPreviousFiveResults()
	{
		searchResult.setText(null);
		int breakpoint = 0;
		
		while (it.hasPrevious())
		{
			if (breakpoint == 10)
				break;
			String docID = it.previous();
			//searchResult.setText(searchResult.getText() + docID + "\n");
			breakpoint ++;
		}
		breakpoint = 0;
		while (it.hasNext())
		{
			if (breakpoint == 5)
				break;
			String docID = it.next();
			searchResult.setText(searchResult.getText() + docID + "\n");
			breakpoint ++;
		}
	}
	public static void startCrawling(String userQuery){
		try{
			String stopWords = "C:/Users/Alok Pal/workspace/searchword/resources/stopwords.txt";
			w.stopWordsConstructor(stopWords);
			w.getQueries(userQuery);
			crawlWebPages("http://www.unt.edu");
			
			
//			processCrawledPages();			
//			returnedLinks = w.retrieveTopDocs();
//			it = returnedLinks.listIterator();
//			int breakPoint = 0;
//			while (it.hasNext())
//			{
//				if (breakPoint == 5)
//						break;
//        		String docId = it.next();
//        		searchResult.setText(searchResult.getText() + docId + "\n");
//        		breakPoint ++;
//			}
		}
		catch (IOException e){}
		}
		
	public static void processCrawledPages() throws IOException
	{
		w.reset();
		for (String link: pagesVisited)
		{
			try{
			Document doc = Jsoup.connect(link).ignoreContentType(true).timeout(0).get();
			webDocs.put(link, doc.text());
			w.readInputFiles(2, doc.text(), link);
			}
			catch (Exception e){}
		}
//		System.out.println(webDocs.size());
	}
	
	public static void crawlWebPages(String URL) throws IOException {
		FileWriter writer = new FileWriter("C:/Users/Alok Pal/workspace/searchword/resources/crawledLinks.txt");
		
		//get useful information
		pagesVisited.add(URL);	//Hash Set
		pagesToVisit.add(URL);	//Queue
 	
		while(pagesToVisit.size() != 0)// || pagesVisited.size() != MAX_PAGES_TO_SEARCH)	
		{
			String nextURL = pagesToVisit.poll();
			try{
				Document doc = Jsoup.connect(nextURL).ignoreContentType(true).timeout(0).get();
				Elements webPages = doc.select("a[href]");
				for(Element link: webPages)
				{
					String urlNext = link.attr("href");
					writer.write(urlNext + "\n");
					//remove "/" , ".htm" from then end of URL string
//					if (urlNext.endsWith(".htm/"))
//						urlNext = urlNext.substring(0, urlNext.length() - 5);
//					if (urlNext.endsWith("/"))
//					{
//						urlNext = urlNext.substring(0, urlNext.length() - 1);
//						//System.out.println(urlNext);
//					}
//					if (urlNext.endsWith(".htm"))
//						urlNext = urlNext.substring(0, urlNext.length() - 4);
					
					if (urlNext.contains("unt.edu") && !urlNext.contains("mailto:"))
					{
						if(!pagesVisited.contains(urlNext))
						{
							String linkTrimmed = link.attr("abs:href");	
//							if (linkTrimmed.endsWith("/"))
//								linkTrimmed = linkTrimmed.substring(0, linkTrimmed.length() - 1);
//							if (linkTrimmed.endsWith(".htm"))
//								linkTrimmed = linkTrimmed.substring(0, linkTrimmed.length() - 4);
							writer.write(linkTrimmed + "\n");
							pagesVisited.add(linkTrimmed);
							pagesToVisit.add(linkTrimmed);
//							if(pagesVisited.size() == 500)
//								{
//									System.out.println("Size of visited set: " + pagesVisited.size());
//									break;
//								}
						}
					}
				}		
			}
			catch (HttpStatusException e)
			{e.printStackTrace();}
		}
	}
}
