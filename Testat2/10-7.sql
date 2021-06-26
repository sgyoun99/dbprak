with
find_all as (
select item_id, sidiff.*, name
from similar_items si, 
	 select_sim_item_id_with_diff_main_cat(item_id, similar_item_id) sidiff
	 inner join category on (diff_main_cat_id = category.category_id)
     --where item_id = 'B0007DDPWU'
)
--insert into diff_main_cat (select * from find_all order by item_id)

--select * from diff_main_cat --852
--select item_id, sim_item_id from diff_main_cat group by item_id, sim_item_id --650
select count(distinct item_id) from diff_main_cat --457
