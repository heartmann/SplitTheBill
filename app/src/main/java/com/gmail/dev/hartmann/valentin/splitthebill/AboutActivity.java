package com.gmail.dev.hartmann.valentin.splitthebill;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView versionTextView = (TextView) findViewById(R.id.textView_version);
        versionTextView.setText(getString(R.string.version_capitalized) + BuildConfig.VERSION_NAME);
        addListeners();
    }

    private void addListeners() {
        findViewById(R.id.textView_valentin).setOnClickListener(
                new MarketOnClickListener("search?q=pub:\"Valentin Hartmann\""));
        findViewById(R.id.textView_contact).setOnClickListener(
                new EMailOnClickListener(this, getString(R.string.support_email_address_valentin),
                        "Feedback [" + getString(R.string.app_name)
                                + ", " + BuildConfig.VERSION_NAME
                                + ", " + Build.MANUFACTURER
                                + ", " + Build.MODEL + "]"));
        findViewById(R.id.textView_rate).setOnClickListener(
                new MarketOnClickListener(
                        "details?id=com.gmail.dev.hartmann.valentin.splitthebill"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MarketOnClickListener implements View.OnClickListener {

        private String link;

        private MarketOnClickListener(String link) {
            this.link = link;
        }

        @Override
        public void onClick(View v) {
            String marketURI = "market://" + link;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(marketURI));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
}
