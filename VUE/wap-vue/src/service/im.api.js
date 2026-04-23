import request from './request'
import { API_PREFIX } from '@/config'

// 获取消息列表
export const _getMsg = (params, orderNo, partyId) => {

    console.log("_getMsg orderNo = " + orderNo);
    console.log("_getMsg partyId = " + partyId);

    let url = `${API_PREFIX}/newOnlinechat!list.action`;
    if (orderNo) {
        url = `${API_PREFIX}/otcOnlinechat!list.action`;
    }
    return request({
        url: url,
        // loading: true,
        method: "GET",
        params: {
            message_id: params.message_id || '',  // 翻页用到
            show_img: params.show_img || true,
            orderNo: orderNo,
            partyId: partyId
        }
    })
};

// 未读消息
export const _getUnreadMsg = (orderNo, partyId) => {
    let url = `${API_PREFIX}/newOnlinechat!unread.action`
    if (orderNo) {
        url = `${API_PREFIX}/otcOnlinechat!unread.action`;
    }
    return request({
        url: url,
        loading: false,
        method: "GET",
        params: {
            orderNo: orderNo,
            partyId: partyId
        }
    })
};

// 发送消息
export const _sendMsg = (type = 'text', content = '', orderNo, partyId) => {
    let url = `${API_PREFIX}/newOnlinechat!send.action`
    if (orderNo) {
        url = `${API_PREFIX}/otcOnlinechat!send.action`;
    }
    return request({
        url: url,
        loading: true,
        method: "GET",
        params: {
            type, // 消息类型， img / text
            content,
            orderNo: orderNo,
            partyId: partyId
        }
    })
};