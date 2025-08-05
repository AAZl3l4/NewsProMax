package com.AAZl3l4.common.feignApi;

import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserServeApiFallbackFactory implements FallbackFactory<UserServeApi> {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public UserServeApi create(Throwable cause) {
        return new UserServeApi() {
            @Override
            public Result<List<User>> list() {
                log.error("调用 user-serve 服务失败，降级处理", cause);
                rabbitTemplate.convertAndSend("error","调用 user-serve 服务失败，降级处理"+ cause);
                throw new RuntimeException("服务降级：获取用户列表失败，请稍后重试", cause);
            }

            @Override
            public Result updateUser(User user) {
                log.error("调用 user-serve 服务失败，降级处理", cause);
                rabbitTemplate.convertAndSend("error","调用 user-serve 服务失败，降级处理"+ cause);
                throw new RuntimeException("服务降级：修改用户，请稍后重试", cause);
            }

            @Override
            public Result getDefault(Integer userId) {
                log.error("调用 user-serve 服务失败，降级处理", cause);
                rabbitTemplate.convertAndSend("error","调用 user-serve 服务失败，降级处理"+ cause);
                throw new RuntimeException("服务降级：获取用户地址，请稍后重试", cause);
            }

            @Override
            public User getUserById(Integer getid) {
                log.error("调用 user-serve 服务失败，降级处理", cause);
                rabbitTemplate.convertAndSend("error","调用 user-serve 服务失败，降级处理"+ cause);
                throw new RuntimeException("服务降级：获取用户信息，请稍后重试", cause);
            }
        };
    }
}