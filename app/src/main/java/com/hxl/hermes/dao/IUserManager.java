package com.hxl.hermes.dao;

import com.hxl.hermes.core.annotion.ClassId;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
@ClassId("com.hxl.hermes.dao.UserManager")
public interface IUserManager {

    Person getPerson();
    void setPerson(Person person);

}
