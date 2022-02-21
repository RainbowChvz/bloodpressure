# bloodpressure
App for tracking blood pressure for 30 days
Developer: Eduardo Chavez

targetSdkVersion 31
minSdkVersion 21
version: 0.0.1

Android Studio Bumblebee 2021.1.1 Patch 1
Openjdk 11.0.11 x64

PROBLEMS:
- Test users need to be added manually in Google Cloud Platform.
    ! Let me know if further test accounts need to be added.
    ! Application is not verified.
- An exception is thrown when attempting to register for data updates of type BLOOD_PRESSURE.
    ! Issue does not occur with TYPE_HEIGHT or TYPE_WEIGHT.
    ! ApiException 5008, constant TRANSIENT_ERROR in FitnessStatusCodes.
        ! According to description, it should work after a retry.
        ! source: https://developers.google.com/android/reference/com/google/android/gms/fitness/FitnessStatusCodes#public-static-final-int-transient_error
    ! HistoryClient API currently supports only a few data types, which do not include blood pressure.
        ! source: https://developers.google.com/android/reference/com/google/android/gms/fitness/HistoryClient#public-taskvoid-registerdataupdatelistener-dataupdatelistenerregistrationrequest-request
    ! Google has recently restricted acces to Health data types, which include blood pressure.
        ! source: https://developers.google.com/fit/scenarios/write-bp-data
