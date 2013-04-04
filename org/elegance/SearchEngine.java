package org.elegance;


import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.IndexWriter;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.MultiFieldQueryParser;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixFilter;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import java.sql.*;

import javax.swing.JPanel;

/**
 * Write a description of class SearchEngine here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class SearchEngine{

	public JPanel panel;

	//final File INDEX_DIR = new File("index");
    RAMDirectory idx;
    IndexWriter writer;
    StandardAnalyzer analyzer;
	//IndexDeletionPolicy deletionPolicy;

    Searcher searcher;
    QueryParser qparser;
    MultiFieldQueryParser mqparser;

    TopDocs topDocs;
    ScoreDoc[] hits;

	//String[] fields;
	public List<String> fields;		//fields to search
	String sql;

    //PreparedStatement pstmt;

    public SearchEngine(DElement fielddef, Connection ldb) {	//throws CorruptIndexException,IOException,ParseException,SQLException
			try{
			panel = new JPanel(null);

			if(fielddef.getAttribute("indextype","RAM").equals("RAM")){
				idx = new RAMDirectory();
				}
			else if(fielddef.getAttribute("analyzer","standard").equals("standard")){
				analyzer = new StandardAnalyzer();
				}
			//else if(fielddef.getAttribute("deletepolicy","lastcommit").equals("lastcommit")){
			//	deletionPolicy = new KeepOnlyLastCommitDeletionPolicy();
			//	}

			//writer = new IndexWriter(idx, analyzer, true, deletionPolicy, IndexWriter.MaxFieldLength.LIMITED);
			writer = new IndexWriter(idx, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);

			searcher = new IndexSearcher(idx);

			//setDefaultOperator(AND_OPERATOR)  //forces an AND operation... not desired for now
			fields = new ArrayList<String>();
			List<DElement> children = new ArrayList<DElement>(fielddef.getElements());
			sql = "SELECT ";
			for(DElement el : children) {
				if(el.getName().equals("FIELD")) {
					fields.add(el.getValue());		//add to the list of searchable fields/columns...
					sql+= el.getValue() + ",";		//
					}
				}
			sql = sql.substring(0,sql.length()-1);		//get rid of the last comma ','
			sql+= " FROM " + fielddef.getAttribute("table");

			System.out.println("\n\tDEBUG:Indexing SQL = " + sql);
			indexRecords(writer, ldb, sql);


			//Multiple Fields
			//String[] fields = {"membername","address"};
			//mqparser = new MultiFieldQueryParser((String[])fields.toArray(),analyzer);
			//String[] queries = {"john*","john*"};      //queries from userinput
			//Query query = mqparser.parse(queries,(String[])fields.toArray(),analyzer);

			//Single Field
			//qparser = new QueryParser("membername",dbtest.getAnalyzer());           //the first argument is the default field for searches
			//Query query = qparser.parse("John");
			//searchResultsSimple(query);

			searchResultsSimple();
			}
		catch(Exception e){
			e.printStackTrace();
			}
        }

	 private void indexRecords(IndexWriter writer, Connection conn, String s) { //throws CorruptIndexException,IOException,ParseException,SQLException
		try{
			//String sql = "SELECT membershipnumber, membername, address FROM members WHERE (membershipnumber IS NOT NULL) AND (membername IS NOT NULL) AND (address IS NOT NULL)";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(s);

			//create a document for each row
			int i=1;
			while (rs.next()) {
				//System.out.println("\nIndexing row: " + i + " Member Name: " + rs.getString(2));

				Document d = new Document();

				d.add(new Field("idnumber", rs.getString(1), Field.Store.YES, Field.Index.NOT_ANALYZED));     //Field.Index.NOT_ANALYZED: Index the field's value without using an Analyzer, so it can be searched.
				d.add(new Field("clientname", rs.getString(2), Field.Store.YES, Field.Index.ANALYZED));                       //Field.Index.ANALYZED: Index the tokens produced by running the field's value through an Analyzer.
				//d.add(new Field("address", rs.getString(3),Field.Store.YES, Field.Index.ANALYZED));      //Store the original field value in the index.

				//BOOSTING TEST
				//subjectField.setBoost(2.2F);

				writer.addDocument(d);

				i++;
				}
			}
		catch(Exception ex){
			ex.printStackTrace();
			}
        }


     public void searchResultsSimple() {	//throws CorruptIndexException,IOException,ParseException

        try{

			//Multiple Fields
			mqparser = new MultiFieldQueryParser((String[])fields.toArray(),analyzer);
			String[] queries = {"john*","john*"};      //queries from userinput
			Query query = mqparser.parse(queries,(String[])fields.toArray(),analyzer);

            /* First parameter is the query to be executed and
               second parameter indicates the no of search results to fetch
             */
            topDocs = searcher.search(query,20);
            // Get an array of references to matched documents
            hits = topDocs.scoreDocs;      //hists for this query

            System.out.println("Query hits " + hits.length + "\n");
            System.out.println("Total hits " + topDocs.totalHits);


            for(ScoreDoc scoredoc: hits){
                //Retrieve the matched document and show relevant details
                Document doc = searcher.doc(scoredoc.doc);
                System.out.println("ID No: "+doc.getField("idnumber").stringValue());
                System.out.println("Member: "+doc.getField("clientname").stringValue());
                //System.out.println("Address: "+ doc.getField("address").stringValue());
                }
            System.out.println("---------------------------------------------");
            }
         catch(Exception exx){
			exx.printStackTrace();
			}

       }





}
