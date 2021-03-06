package com.windsor.foodapp.dao;

import com.windsor.foodapp.model.FoodItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FoodItemDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Map<String, List<FoodItem>> getFoodItemByRestaurant(int restaurantId) throws Exception {
        List<FoodItem> foodItemList = new ArrayList<>();
        List<Map<String, Object>> stringObjectMap = jdbcTemplate.queryForList("select f.* from fooditem f inner join restaurant r on r.id = f.restaurantid where restaurantid= ?", restaurantId);
        if (stringObjectMap.isEmpty())
            throw new Exception("No such Food Item found");
        for (Map<String, Object> result : stringObjectMap) {
            FoodItem foodItem = convertSQLResultToFoodItem(result);
            foodItemList.add(foodItem);
        }

        Map<String,List<FoodItem>> categoryToFoodItemMap = new HashMap<>();

        for(FoodItem foodItem : foodItemList) {
            if(categoryToFoodItemMap.get(foodItem.getCategory()) == null) {
                List<FoodItem> foodItems = new ArrayList<>();
                foodItems.add(foodItem);
                categoryToFoodItemMap.put(foodItem.getCategory(), foodItems);

            } else {List<FoodItem> foodItems = categoryToFoodItemMap.get(foodItem.getCategory());
            foodItems.add(foodItem);
            categoryToFoodItemMap.put(foodItem.getCategory(), foodItems);
            }

        }




        return categoryToFoodItemMap;

    }


    public Map<Integer, FoodItem> getFoodItemsForIds(String ids) {
        Map<Integer, FoodItem> returnMap = new HashMap<>();
        String sql = "select * from fooditem where id in (" + ids + ")";
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
        for(Map<String, Object> result : resultList) {
            returnMap.put(Integer.parseInt(result.get("id").toString()), convertSQLResultToFoodItem(result));
        }
        return returnMap;
    }

    private FoodItem convertSQLResultToFoodItem(Map<String, Object> result) {
        FoodItem foodItem = new FoodItem(Integer.parseInt(result.get("id").toString()), result.get("name").toString(), Double.parseDouble(result.get("cost").toString()),
                result.get("category").toString(), Integer.parseInt(result.get("restaurantid").toString()), Integer.parseInt(result.get("foodcourtid").toString()),
                Integer.parseInt(result.get("timetoprepareinminutes").toString()), result.get("restaurantname").toString(), result.get("foodcourtname").toString());
        return foodItem;
    }
}
