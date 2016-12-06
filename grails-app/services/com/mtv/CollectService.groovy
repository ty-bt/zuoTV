package com.mtv

import grails.transaction.Transactional
import org.springframework.util.Assert

@Transactional
class CollectService {

    /**
     * 添加收藏
     * @param userId
     * @param roomId
     * @param groupId
     * @return
     */
    public Collect add(Long userId, Long roomId, Long groupId = null){
        Assert.notNull(userId, "用户ID不能为空")
        Assert.notNull(roomId, "房间ID不能为空")

        User user = User.get(userId)
        Assert.notNull(user, "用户不存在")
        int count = Collect.countByUser(user)
        Assert.isTrue(count <= 120, "收藏数量达到上限")

        Room room = Room.get(roomId)
        Assert.notNull(room, "房间不存在")
        CollectGroup group = groupId ? CollectGroup.get(groupId) : null
        Collect old = Collect.findByUserAndRoom(user, room)
        if(old){
            return old
        }

        Collect collect = new Collect()
        collect.user = user
        collect.room = room
        collect.collectGroup = group
        collect.save()
    }

    public void delete(Long id, Long userId = null){
        Assert.notNull(id, "收藏ID不能为空")
        Collect collect = Collect.get(id)
        Assert.notNull(collect, "没有找到对应的收藏")
        if(userId){
            if(userId != collect.user.id){
                Assert.notNull(collect, "没有找到对应的收藏")
            }
        }
        collect.delete()
    }
}
