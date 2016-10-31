package com.mtv

import grails.transaction.Transactional
import org.springframework.util.Assert

@Transactional
class CollectGroupService {

    /**
     * 添加分组
     * @param userId
     * @param name 分组名称
     * @param type 分组类型 默认为普通收藏夹
     * @return
     */
    public CollectGroup add(Long userId, String name, String type = CollectGroup.COLLECT_TYPE.Collect.value){
        Assert.notNull(name, "分组名不能为空")
        User user = User.get(userId)
        Assert.notNull(user, "没有找到用户")
        if(CollectGroup.findByUserAndName(user, name)){
            throw new Exception("分组名称重复")
        }
        CollectGroup collectGroup = new CollectGroup()
        collectGroup.user = user
        collectGroup.name = name
        collectGroup.type = type
        return collectGroup.save()
    }

    /**
     * 删除收藏夹
     * @param userId 用户ID
     * @param groupId 收藏夹ID
     */
    public void delete(Long userId, Long groupId){
        User user = User.get(userId)
        Assert.notNull(user, "没有找到用户")
        CollectGroup group = CollectGroup.findByUserAndId(user, groupId)
        Assert.notNull(group, "不存在的收藏夹")
        group.delete()
    }


}
