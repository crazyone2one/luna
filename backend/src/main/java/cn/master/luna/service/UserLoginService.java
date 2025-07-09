package cn.master.luna.service;

import cn.master.luna.controller.AuthController;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.dto.UserDTO;
import cn.master.luna.handler.ResultHandler;

/**
 * @author Created by 11's papa on 2025/7/8
 */
public interface UserLoginService {
    ResultHandler login(AuthController.LoginRequest request);

    void updateUser(SystemUser user);

    UserDTO getUserDTO(String userId);
}
