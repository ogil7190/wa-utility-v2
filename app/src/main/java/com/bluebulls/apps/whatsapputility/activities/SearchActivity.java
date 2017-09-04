package com.bluebulls.apps.whatsapputility.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
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
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends Activity {

    private SearchView searchView;
    private WebView webView;
    private Button search, cancel;
    private ListView suggestionList;
    private TextView empty_search;
    private ArrayList<Query> queries = new ArrayList<>();
    private SearchAdapter adapter;
    private String orig_empty_search_str;
    private CircularProgressView progress;
    private SwitchCompat contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SearchFloat);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progress = (CircularProgressView) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        suggestionList = (ListView) findViewById(R.id.suggestions);
        suggestionList.setEmptyView(findViewById(R.id.search_empty));
        searchView = (SearchView)findViewById(R.id.search);
        empty_search = (TextView) findViewById(R.id.search_empty);
        orig_empty_search_str = empty_search.getText().toString();

        contacts = (SwitchCompat) findViewById(R.id.contactSearch);

        webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.GONE);

        search = (Button) findViewById(R.id.submitSearch);
        cancel = (Button)findViewById(R.id.closeSearch);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!contacts.isChecked()) {
                    if (searchView.getQuery().length() > 0)
                        searchWeb(searchView.getQuery().toString());
                }
                else
                    Toast.makeText(getApplicationContext(), "Contacts Saerch Coming Soon!", Toast.LENGTH_SHORT).show();
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
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress<100) {
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(newProgress);
                    progress.startAnimation();
                }
                else {
                    progress.setVisibility(View.GONE);
                    progress.resetAnimation();
                }
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
                if(!contacts.isChecked()) {
                    searchView.setQuery(query, false);
                    suggestionList.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);
                    empty_search.setText("Loading...");
                    searchWeb(query);
                }
                else
                    Toast.makeText(getApplicationContext(), "Contacts Saerch Coming Soon!", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if(contacts.isChecked()) {
                       // Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_SHORT).show();
                    }
                    else
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
                suggestionList.setVisibility(View.GONE);
                searchWeb(queries.get(position).getQuery());
            }
        });
    }

    private void searchWeb(String query){
        if(!query.equals("")) {
            webView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            empty_search.setVisibility(View.GONE);
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

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    private void handleResponse(String response, String query){
        suggestionList.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

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
        empty_search.setText(orig_empty_search_str);
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
