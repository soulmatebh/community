package com.soulmate.community.dao;

import com.soulmate.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author zhengguohuang
 * @date 2021/3/15
 */
@Mapper
//@Deprecated
public interface LoginTicketMapper {
    /**
     * 插入login_ticket表（通过注解不用谢xml SQL查询文件）
     *
     * @param loginTicket
     * @return
     */
    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 通过Ticket查找记录
     *
     * @param ticket
     * @return
     */
    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 更新login_ticket的status字段
     *
     * @param ticket
     * @param status
     * @return
     */
    @Update({
            "update login_ticket set status = #{status} where ticket=#{ticket} "
    })
    int updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
