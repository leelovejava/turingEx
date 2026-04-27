/**
 *
 */
package com.yami.trading.bean.item.mapstruct;


import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.dto.ItemConfig;
import com.yami.trading.bean.item.dto.ItemDTO;
import com.yami.trading.common.mapstruct.EntityWrapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 *  ItemWrapper
 * @author lucas
 * @version 2023-03-10
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {})
public interface ItemWrapper extends EntityWrapper<ItemDTO, Item> {

    ItemWrapper INSTANCE = Mappers.getMapper(ItemWrapper.class);

    Item toEntity(ItemDTO dto);


    ItemDTO toDTO(Item entity);

    Item toEntity(ItemConfig itemConfig);


}

