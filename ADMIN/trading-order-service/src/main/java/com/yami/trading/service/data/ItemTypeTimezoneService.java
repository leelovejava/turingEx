package com.yami.trading.service.data;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemTypeTimezoneService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Getter
    private  Map<String, String> itemTypeTimezoneMap;

    @PostConstruct
    public void init(){
        itemTypeTimezoneMap = list();
    }


    public Map<String, String> list() {
        String sql = "SELECT item_type, timezone FROM t_item_type_timezone";

        List<ItemTypeTimezone> resultList = jdbcTemplate.query(sql, new ItemTypeTimezoneRowMapper());

        // 使用Java 8 Stream将结果列表转换为Map
        itemTypeTimezoneMap = resultList.stream()
                .collect(Collectors.toMap(ItemTypeTimezone::getItemType, ItemTypeTimezone::getTimezone));

        return itemTypeTimezoneMap;
    }

    private static class ItemTypeTimezoneRowMapper implements RowMapper<ItemTypeTimezone> {
        @Override
        public ItemTypeTimezone mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            ItemTypeTimezone itemTypeTimezone = new ItemTypeTimezone();
            itemTypeTimezone.setItemType(resultSet.getString("item_type"));
            itemTypeTimezone.setTimezone(resultSet.getString("timezone"));
            return itemTypeTimezone;
        }
    }
}