import AddPanel from './headercontrol/add-panel.vue'
import Table from './table/table.vue'
import UploadImage from './upload/image-upload.vue'
import PreviewImage from './preview/preview.vue'
import Uploader from './uploader/uploader.vue'
import HouseDetails from './housedetails/housedetails.vue'
import UserInfo from './userInfo/index.vue'
import MsgSubmit from './msgSubmit/msgSubmit'
import CommentList from './commentList/commentList'
import Push from './push/push'
const yztzComponents = {
    install:function (Vue) {
        Vue.component('yztz-add-panel',AddPanel),
        Vue.component('yztz-table',Table),
        Vue.component('yztz-upload-image',UploadImage),
        Vue.component('yztz-preview-image',PreviewImage)
        Vue.component('yztz-uploader',Uploader)
        Vue.component('yztz-house-details',HouseDetails)
        Vue.component('yztz-user-info',UserInfo)
        Vue.component('yztz-msg-submit',MsgSubmit)
        Vue.component('yztz-comment-list',CommentList)
        Vue.component('yztz-push',Push)
    }
};

export default yztzComponents
