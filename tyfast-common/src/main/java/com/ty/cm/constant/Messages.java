package com.ty.cm.constant;

/**
 * 友好的业务消息提示
 *
 * @Author Tommy
 * @Date 2022/2/6
 */
public interface Messages {

    String RELATED_DATA_DELETE = "存在关联数据，删除失败";

    String NO_OPERATION = "没有进行任何操作";

    /** 验证码错误消息 **/
    String ERROR_MSG_CAPTCHA = "验证码错误！";

    /** 账户被冻结错误消息 **/
    String ERROR_MSG_ACCOUNT_LOCKED = "账户被冻结，请联系管理员！";

    /** 用户名与密码错误消息 **/
    String ERROR_MSG_ACCOUNT = "用户名或密码错误！";

    /** 用户名或密码为空消息 **/
    String ERROR_MSG_ACCOUNT_EMPTY = "用户名或密码不能为空！";

    /** 账户不存在错误消息 **/
    String ERROR_MSG_ACCOUNT_NON_EXIST = "账户不存在！";

    /** 未知账户错误消息 **/
    String ERROR_MSG_ACCOUNT_UNKNOWN = "未知账户！";

    /** 程序异常消息 **/
    String ERROR_MSG_EXCEPTION = "服务器暂时无响应，请稍后重试！";

    /** 账号已存在 **/
    String EXISTS_LOGIN_NAME = "账号已存在，请更换";

    /** 原密码错误 **/
    String ERROR_PASSWORD = "原密码错误";

    /** ID不能为空 **/
    String EMPTY_ID = "ID不能为空";

    /** ID已存在 **/
    String EXISTS_ID = "ID已存在，请修改";

    /** 字典Code已存在 **/
    String EXISTS_DICT_CODE = "字典Code已存在，请修改";

    /** 字典项的值重复 **/
    String EXISTS_DUPLICATE_ITEM_VALUE = "字典项的值不能重复";
}
