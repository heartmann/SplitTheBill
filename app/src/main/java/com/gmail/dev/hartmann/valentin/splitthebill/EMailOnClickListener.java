package com.gmail.dev.hartmann.valentin.splitthebill;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class EMailOnClickListener implements View.OnClickListener{

    private Activity parent;
    private String recipient, subject;

    public EMailOnClickListener(Activity parent, String recipient, String subject) {
        this.parent = parent;
        this.recipient = recipient;
        this.subject = subject;
    }

    public EMailOnClickListener(Activity parent, String recipient) {
        this(parent, recipient, null);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        if (subject != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (intent.resolveActivity(parent.getPackageManager()) != null) {
            parent.startActivity(intent);
        }
    }
}
