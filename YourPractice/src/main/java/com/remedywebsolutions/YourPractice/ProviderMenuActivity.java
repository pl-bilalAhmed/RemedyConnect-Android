package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.readystatesoftware.viewbadger.BadgeView;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderCallsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderUnreadCallsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetProviderCallsRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetProviderUnreadCallsRequest;
import com.remedywebsolutions.YourPractice.parser.MainParser;

import java.io.IOException;
import java.util.ArrayList;

public class ProviderMenuActivity extends DefaultActivity implements View.OnClickListener , RequestListener<GetProviderUnreadCallsResponse>{
    ArrayList<Button> menuButtons;
    BadgeView badge;
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Main menu");
        setContentView(R.layout.activity_provider_menu);
        Skin.applyMainMenuBackground(this);
        Skin.applyProviderMenuButtons(this);
        Skin.applyThemeLogo(this, true);
        if (extras != null && extras.getBoolean("isRoot")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
        menuButtons = new ArrayList<Button>();
        View target =  findViewById(R.id.menuButton3);
        badge = new BadgeView(this, target);
        menuButtons.add((Button) this.findViewById(R.id.menuButton3));
        menuButtons.add((Button) this.findViewById(R.id.menuButton4));

        for (Button button : menuButtons) {
            button.setOnClickListener(this);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
            case android.R.id.home:
                // In this app, we can just simple force the Up button to behave the same way as the Back.
              //  this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        if(Data.IsRegistered(getApplicationContext())) {
            GetProviderUnreadCallsRequest cntReq = new GetProviderUnreadCallsRequest(this);
            spiceManager.execute(cntReq,  this);

            //   updateNumberOfInboxItems(inbox.size());
            progress.dismiss();
        }
        else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        super.onStart();

    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    void onClickFireActivity(Integer index) {
      setTitle(Integer.toString(index));
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.menuButton3:

                Intent intent = new Intent(this, SecureCallListActivity.class);
                startActivity(intent);
                break;
            case R.id.menuButton4:
                onClickFireActivity(3);
                MainViewController.FireBrowser(this, "https://admin.remedyoncall.com");

                break;

        }
    }


        @Override
        public void onRequestFailure(SpiceException e) {
            progress.dismiss();
            reloginSpiceFailureHandler(e);
        }

        @Override
        public void onRequestSuccess(GetProviderUnreadCallsResponse inboxItemsResponse) {
            if(inboxItemsResponse.successfull) {
                if(inboxItemsResponse.count > 0) {
                    badge.setText(Integer.toString(inboxItemsResponse.count));
                    badge.setTextSize(16);
                    badge.setBadgeMargin(20);
                    badge.show();
                }

            }
        }


    private void reloginSpiceFailureHandler(SpiceException e) {
        spiceManager.cancelAllRequests();
        if (e.getCause() instanceof IOException) {
            // We should have an authentication failure here, so re-login the user...
            new AlertDialog.Builder(ProviderMenuActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Your session has expired")
                    .setMessage("You will need to log in again. Please OK to proceed.")
                    .setPositiveButton("OK", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent loginIntent = new Intent(ProviderMenuActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }
                    })
                    .show();
        }
    }
}
