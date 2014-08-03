package com.redriver.measurements.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.redriver.measurements.data.TaskContract;
import com.redriver.measurements.io.R;

/**
 * Created by zwq00000 on 2014/7/27.
 */
public class SearchableActivity extends Activity {
    private String mUserQuery;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable);
        if (savedInstanceState == null && !initActivityState(getIntent())) {
            //finish();
            return;
        }

        SearchManager searchServer = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        //searchServer.getGlobalSearchActivity()
    }

    private boolean initActivityState(Intent intent) {
        if (TextUtils.equals(Intent.ACTION_VIEW, intent.getAction())) {
            long noteId = intent.getLongExtra(Intent.EXTRA_UID, 0);
            mUserQuery = "";

            /**
             * Starting from the searched result
             */
            if (intent.hasExtra(SearchManager.EXTRA_DATA_KEY)) {
                noteId = Long.parseLong(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
                mUserQuery = intent.getStringExtra(SearchManager.USER_QUERY);

                Uri queryUri = Uri.withAppendedPath(TaskContract.Task.CONTENT_URI, mUserQuery);
                //getContentResolver().query(queryUri,)
            }

        }
        return false;
    }
}