package com.bluebulls.apps.whatsapputility.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.adapters.SearchAdapter;
import com.bluebulls.apps.whatsapputility.entity.actors.Query;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends Activity {

    private SearchView searchView;
    private WebView webView;
    private Button search, cancel;
    private ListView suggestionList;

    private ArrayList<Query> queries = new ArrayList<>();
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SearchFloat);

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_search);

        suggestionList = (ListView) findViewById(R.id.suggestions);
        suggestionList.setEmptyView(findViewById(R.id.search_empty));
        searchView = (SearchView)findViewById(R.id.search);

        webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.GONE);

        search = (Button) findViewById(R.id.submitSearch);
        cancel = (Button)findViewById(R.id.closeSearch);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchView.getQuery().length()>0)
                    searchWeb(searchView.getQuery().toString());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Activity activity = this;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                activity.setProgress(newProgress*1000);
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Error:" + description, Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery(query,false);
                suggestionList.setVisibility(View.GONE);
                searchWeb(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    suggestion(newText);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        adapter = new SearchAdapter(queries,this);
        suggestionList.setAdapter(adapter);

        suggestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(queries.get(position).getQuery(),false);
                suggestionList.setVisibility(View.GONE);
                searchWeb(queries.get(position).getQuery());
            }
        });
    }

    private void searchWeb(String query){
        if(!query.equals("")) {
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl("https://www.google.com/search?site=&source=hp&q=" + query);
        }
    }

    private void suggestion(final String query) throws UnsupportedEncodingException {
        if(!query.equals("") && !query.equals(null)) {
            String url = "http://suggestqueries.google.com/complete/search?client=firefox&q=" + URLEncoder.encode(query,"UTF-8");

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            handleResponse(response, query);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(stringRequest);
        }
    }

    private void handleResponse(String response, String query){
        suggestionList.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);

        response = response.replace("["+"\""+query+"\""+",[","");
        response = response.replace("]]","");
        response = response.replace("\"","");
        queries.clear();

        String s = "";
        for(int i=0; i<response.length(); i++){
                if (response.charAt(i) != ',') {
                    s = s + response.charAt(i);
                } else {
                    queries.add(new Query(s, query));
                    s = "";
                }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        }
        else
        super.onBackPressed();
    }
}
