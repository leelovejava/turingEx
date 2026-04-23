import { post, get, upload } from '@/utils/request'
import httpRequest from '@/utils/httpRequest'
import {  Notification } from 'element-ui'

// 获取聊天列表服务接口
export const ServeGetTalkList = data => {
  return get('/api/v1/talk/list', data)
}

// 聊天列表创建服务接口
export const ServeCreateTalkList = data => {
  return post('/api/v1/talk/create', data)
}

// 删除聊天列表服务接口
export const ServeDeleteTalkList = data => {
  return post('/api/v1/talk/delete', data)
}

// 对话列表置顶服务接口
export const ServeTopTalkList = data => {
  return post('/api/v1/talk/topping', data)
}

// 清除聊天消息未读数服务接口
export const ServeClearTalkUnreadNum = data => {
  return post('/api/v1/talk/unread/clear', data)
}

// 获取聊天记录服务接口
export const ServeTalkRecords = data => {
  return get('/api/v1/talk/records', data)
}

// 获取转发会话记录详情列表服务接口
export const ServeGetForwardRecords = data => {
  return get('/api/v1/talk/records/forward', data)
}

// 对话列表置顶服务接口
export const ServeSetNotDisturb = data => {
  return post('/api/v1/talk/disturb', data)
}

// 查找用户聊天记录服务接口
export const ServeFindTalkRecords = data => {
  return get('/api/v1/talk/records/history', data)
}

// 搜索用户聊天记录服务接口
export const ServeSearchTalkRecords = data => {
  return get('/api/v1/talk/search-chat-records', data)
}

export const ServeGetRecordsContext = data => {
  return get('/api/v1/talk/get-records-context', data)
}

// 发送代码块消息服务接口
export const ServeSendTalkText = data => {
  return post('/api/v1/talk/message/text', data)
}

// 发送代码块消息服务接口
export const ServeSendTalkCodeBlock = data => {
  return post('/api/v1/talk/message/code', data)
}

// 发送聊天文件服务接口
export const ServeSendTalkFile = data => {
  return post('/api/v1/talk/message/file', data)
}

// 发送聊天图片服务接口
export const ServeSendTalkImage = data => {
  return upload('/api/v1/talk/message/image', data)
}

// 发送表情包服务接口
export const ServeSendEmoticon = data => {
  return post('/api/v1/talk/message/emoticon', data)
}

// 转发消息服务接口
export const ServeForwardRecords = data => {
  return post('/api/v1/talk/message/forward', data)
}

// 撤回消息服务接口
export const ServeRevokeRecords = data => {
  return post('/api/v1/talk/message/revoke', data)
}

// 删除消息服务接口
export const ServeRemoveRecords = data => {
  return post('/api/v1/talk/message/delete', data)
}

// 收藏表情包服务接口
export const ServeCollectEmoticon = data => {
  return post('/api/v1/talk/message/collect', data)
}

export const ServeSendVote = data => {
  return post('/api/v1/talk/message/vote', data)
}

export const ServeConfirmVoteHandle = data => {
  return post('/api/v1/talk/message/vote/handle', data)
}

//----------------------------------------------------------------

export const Notification1 = (msg) =>{
  Notification({
    message: msg,
    position: "top-right",
    type: 'error',
  });
}


//创建新用户消息列表 /public/newAdminOnlineChatAction!create.action
export const createNewAdminOnlineChatAction = (params,call) => {
  //
  httpRequest({
    url: httpRequest.adornUrl('/public/newAdminOnlineChatAction!create.action'),
    method: 'get',
    params: httpRequest.adornParams(Object.assign(
        {
        },
        params
    )),
    data: httpRequest.adornData(
      Object.assign(
        {
        },
        params
      )
    )
  }).then(({ data }) => {
    console.log("createNewAdminOnlineChatAction data = " + JSON.stringify(data))
    if(call){call(data)}
  })
  //
}
//在线聊天-人员列表 /public/newAdminOnlineChatAction!userlist.action
export const userlistNewAdminOnlineChatAction = (params,call) => {
  //
  httpRequest({
    url: httpRequest.adornUrl('/public/newAdminOnlineChatAction!userlist.action'),
    method: 'get',
    params: httpRequest.adornParams(Object.assign(
        {
        },
        params
    )),
    data: httpRequest.adornData(
      Object.assign(
        {
        },
        params
      )
    )
  }).then(({ data }) => {
    if(call){call(data)}
    if(data.code != 0){
      Notification1('获取在线聊天人员列表失败:' + JSON.stringify(data));
    }
  }).catch(err => {
    console.log("err => " + err)
    if(call){call(undefined)}

    // Notification1('获取在线聊天人员列表失败:检测网络');
    Notification1('正在加载中');
    //

  })
  //
}
//聊天记录列表 /public/newAdminOnlineChatAction!list.action
export const listNewAdminOnlineChatAction = (params,call) => {
  if(!params.partyid){
    console.log("params.partyid === "+ params.partyid)
    if(call){call(undefined)}
    return;
  }
  //
  httpRequest({
    url: httpRequest.adornUrl('/public/newAdminOnlineChatAction!list.action'),
    method: 'get',
    params: httpRequest.adornParams(Object.assign(
        {
        },
        params
    )),
    data: httpRequest.adornData(
      Object.assign(
        {
        },
        params
      )
    )
  }).then(({ data }) => {
    if(call){call(data)}
    if(data.code != 0){
      Notification1('获取在线聊天记录失败:' + JSON.stringify(data));
    }
  }).catch(err => {
    console.log("err => " + err)
    if(call){call(undefined)}
    // Notification1('获取在线聊天记录失败:检测网络');
  })
  //
}
//获取用户信息 /public/newAdminOnlineChatAction!getUserInfo.action
export const getUserInfoNewAdminOnlineChatAction = (params,call) => {
  //
  httpRequest({
    url: httpRequest.adornUrl('/public/newAdminOnlineChatAction!getUserInfo.action'),
    method: 'get',
    params: httpRequest.adornParams(Object.assign(
        {
        },
        params
    )),
    data: httpRequest.adornData(
      Object.assign(
        {
        },
        params
      )
    )
  }).then(({ data }) => {
    if(call){call(data)}
    if(data.code != 0){
      Notification1('获取用户信息失败:' + JSON.stringify(data));
    }
  }).catch(err => {
    console.log("err => " + err)
    if(call){call(undefined)}
    // Notification1('获取用户信息失败:检测网络');
  })
  //
}
//发送消息 /public/newAdminOnlineChatAction!send.action
export const sendNewAdminOnlineChatAction = (params,call) => {
  httpRequest({
    url: httpRequest.adornUrl('/public/newAdminOnlineChatAction!send.action'),
    method: 'post',
    params: httpRequest.adornParams(Object.assign(
        {
        },
        params
    )),
    data: httpRequest.adornData(
      Object.assign(
        {
        },
        params
      )
    )
  }).then(({data}) => {
    if(call){call(data)}
    console.log("chat => "+JSON.stringify(data));
    if(data.code == 1){
      Notification({
        title: '消息',
        message: data.msg,
        type: 'warning',
      })
    }
  })
  //
}
//查询未读消息 /public/newAdminOnlineChatAction!unread.action
export const unreadNewAdminOnlineChatAction = (params,call,errCall) => {
  //
  httpRequest({
    url: httpRequest.adornUrl('/public/newAdminOnlineChatAction!unread.action'),
    method: 'get',
    params: httpRequest.adornParams(Object.assign(
        {
        },
        params
    )),
    data: httpRequest.adornData(
      Object.assign(
        {
        },
        params
      )
    )
  }).then(({ data }) => {
    if(call){call(data)}
  }).catch((e) => {
    if(errCall){errCall(e)}
  })
  //
}
//删除聊天会话 /public/newAdminOnlineChatAction!del.action
export const delNewAdminOnlineChatAction = (params,call) => {
  //
  httpRequest({
    url: httpRequest.adornUrl('/public/newAdminOnlineChatAction!del.action'),
    method: 'get',
    params: httpRequest.adornParams(Object.assign(
        {
        },
        params
    )),
    data: httpRequest.adornData(
      Object.assign(
        {
        },
        params
      )
    )
  }).then(({ data }) => {
    // console.log("data = " + JSON.stringify(data))
    if(call){call(data)}
  })
  //
}
//后台客服撤回消息操作 /public/newAdminOnlineChatAction!deleteOnlineChatMessage.action
export const deleteOnlineChatMessageNewAdminOnlineChatAction = (params,call) => {
  //
  httpRequest({
    url: httpRequest.adornUrl('/public/newAdminOnlineChatAction!deleteOnlineChatMessage.action'),
    method: 'get',
    params: httpRequest.adornParams(Object.assign(
        {
        },
        params
    )),
    data: httpRequest.adornData(
      Object.assign(
        {
        },
        params
      )
    )
  }).then(({ data }) => {
    // console.log("data = " + JSON.stringify(data))
    if(call){call(data)}
  })
  //
}

//设置备注
export const resetRemarksNewAdminOnlineChatAction = (params,call) => {
  //
  httpRequest({
    url: httpRequest.adornUrl('/public/newAdminOnlineChatAction!resetRemarks.action'),
    method: 'get',
    params: httpRequest.adornParams(Object.assign(
        {
        },
        params
    )),
    data: httpRequest.adornData(
      Object.assign(
        {
        },
        params
      )
    )
  }).then(({ data }) => {
    // console.log("data = " + JSON.stringify(data))
    if(call){call(data)}
  })
  //
}
//resetRemarks.action"