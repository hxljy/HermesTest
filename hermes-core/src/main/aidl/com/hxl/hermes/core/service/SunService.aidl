// SunHermeService.aidl
package com.hxl.hermes.core.service;

import com.hxl.hermes.core.service.request;
import com.hxl.hermes.core.service.Response;

interface SunService {
    Response send(in Request request);
}
