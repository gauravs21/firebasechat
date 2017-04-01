package com.chat.app.fcm;

import com.chat.app.utility.PrefsUtil;
import com.google.firebase.iid.FirebaseInstanceIdService;

/*
 * Created by kopite on 27/3/17.
 */

public class FirebaseInstanceId extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = com.google.firebase.iid.FirebaseInstanceId.getInstance().getToken();
        PrefsUtil.FcmToken(this,refreshedToken);
    }
}
