package service;

import pojo.ItemsCustom;
import pojo.ItemsQueryVo;

import java.util.List;

/**
 * 商品的service接口
 */

public interface ItemsService {

    //商品删除信息
    void deleteItems(Integer id) throws Exception;

    //商品的查询列表
    List<ItemsCustom> findItemsList(ItemsQueryVo itemsQueryVo)
            throws Exception;

    //根据商品id查询商品信息
    ItemsCustom findItemsById(int id) throws Exception;

    //更新商品信息
    /**
     * 定义service接口，遵循单一职责，将业务参数细化(不要使用包装类型，比如map)
     * @param id 修改商品的id
     * @param itemsCustom 修改商品的信息
     * @throws Exception
     */
    void updateItems(Integer id, ItemsCustom itemsCustom) throws Exception;

}
