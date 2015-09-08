package com.open.aidl.service;

import com.open.aidl.service.ITestServiceCallback;

interface ITestService {

    void registerCallback(ITestServiceCallback cb);
    
    void unregisterCallback(ITestServiceCallback cb);
    
    int request(inout Bundle mBundle);
}
