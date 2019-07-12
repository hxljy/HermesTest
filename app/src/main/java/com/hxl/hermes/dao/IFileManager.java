package com.hxl.hermes.dao;

import com.hxl.hermes.core.annotion.ClassId;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/29 0029.
 */
@ClassId("com.hxl.hermes.dao.FileManager")
public interface IFileManager {

    public String getPath();

    public void setPath(String path);
}
