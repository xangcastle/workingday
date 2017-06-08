package com.valuarte.dtracking.Util;

import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.api.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

/**
 * Created by Jose Williams Garcia on 31/5/2017.
 */

@EBean
public class MyRestErrorHandler implements RestErrorHandler {
    String TAG="MyRestErrorHandler";
    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        // Do whatever you want here.
        Log.i(TAG,e.getMessage() );
    }
}
