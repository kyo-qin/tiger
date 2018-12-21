package org.tiger.guava.common;

import java.util.List;






/**
 * Description 配置信息DAO
 * 
 * @ClassName NPConfigDao
 *
 * @Copyright 炫彩互动
 * 
 * @Project egame.netpay.core
 * 
 * @Author ota
 * 
 * @Create Date 2017年7月18日
 * 
 * @Modified by none
 *
 * @Modified Date
 */
public interface NPConfigDao {

    public NPConfigInfo getNPConfigInfoByConfigCodeAndType(String configCode,int configType);
    
    public List<NPConfigInfo> getAllNPConfigInfos();

}
