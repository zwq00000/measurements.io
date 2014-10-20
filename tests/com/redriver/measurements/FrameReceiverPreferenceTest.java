package com.redriver.measurements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.test.AndroidTestCase;
import com.redriver.measurements.io.R;

/**
 * Created by zwq00000 on 2014/10/19.
 */
public class FrameReceiverPreferenceTest extends AndroidTestCase {

    public void showPreferences() throws Exception{
        Context context = getContext();
        //Intent intent = new Intent(context, FragmentPreferences.class);
        //context.startActivity(intent);

        Thread.sleep(1000*10);
    }

}
