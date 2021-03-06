package com.chaveze.bloodpressure;

public interface IConstants {
    int AUTHSTEP_INIT                           = 100;
    int AUTHSTEP_PERMISSIONS                    = 101;
    int AUTHSTEP_DATA_REQUEST                   = 102;
    int AUTHSTEP_INIT_LISTENER                  = 103;
    int AUTHSTEP_REGISTER_LISTENER              = 104;

    int REQUESTCODE_OAUTH20                     = 200;

    int RESULTCODE_SUCCESS_NO_HISTORY           = 2;
    int RESULTCODE_SUCCESS_GRANTED              = 1;
    int RESULTCODE_SUCCESS                      = 0;
    int RESULTCODE_ERROR_DEFAULT                = -1;

    int RESULTCODE_GOOGLE_PERMISSION_GRANTED    = -1;
    int RESULTCODE_GOOGLE_PERMISSION_DISMISS    = 0;

    int TIMEOUT_DATA_REQUEST                    = 6*1000;

    String NOTIFICATION_CHANNEL_ID              = "DefaultChannel";

}
