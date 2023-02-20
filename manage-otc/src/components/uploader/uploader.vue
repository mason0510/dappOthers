<template>
  <div id="container">
    <a id="pickfiles" href="javascript:;">请选择文件</a>
    <a id="uploadfiles" href="javascript:;">上传</a>
 </div>

</template>
<script>


  import plupload from 'plupload'

  //  import NProgress from 'nprogress'
  //  import 'nprogress/nprogress.css'
  //  import Nanobar from 'nanobar'

  //  let nanobar = new Nanobar({id:'progressBar', classname:'', target:''})  // {id, classname, target}

  export default {
    props: {
      uploadUrl : String,
      uploadType : Number
    },
    data () {
      return {
        uploader     : null,
        serverUrl    : this.uploadUrl,  //todo 解开, 用 {params} 传
        total_percent: 0
      }
    },

    computed: {},
    methods: {
      async doUpload () {
//        console.log('exec => async doUpload () ', this)
        await this.uploader.start()
      },
      async doRemove (idx) {

        if (this.uploader.files[idx].percent === 100) {
          console.log(`已经完成 100%，不可移除！`)
        }
        else {
          this.added_files.splice(idx, 1)
          this.uploader.splice(idx, 1)
        }

        //todo 如果是编辑模式，还需要删除远端文件

      }
    },

    created () {

    },

    mounted () {

      this.uploader = new plupload.Uploader({
        runtimes       : 'html5',
        browse_button  : 'pickfiles',                           // DOM's ID 或 DOM 本身
        container      : document.getElementById('container'),  // DOM's ID 或 DOM 本身
        multi_selection: false,                                // ===1 不可多选。 另，移动端不准备提供 flash/silverlight支持，pc端可打开

        init: {
          Init        : (up) => {
            up.setOption({
              filters: {
                max_file_size     : '10mb',
                prevent_duplicates: true,     //不允许选取重复文件
                mime_types        : [{title: '图片文件', extensions: 'jpg,jpeg,gif,png,webp'}]    //如果Init前传入设置，会导致部分机型、微信浏览器无法选择文件
              },
            })
          },
          PostInit    : () => {
            //document.getElementById(this.notice_id).innerHTML = ''
          },
          FilesAdded  : (up, files) => {
            plupload.each(files, (file) => {
              file.src = URL.createObjectURL(file.getNative())        //本意应该用 plupload 自带 mOxie 对象来做，但各种尝试都不奏效。最终将 plupload file 对象与 HTML5 方法完美结合。
              this.added_files.push(file)  //TODO 会重塑变异数组
            })
          },
          BeforeUpload: (up, file) => {

            // let new_multipart_params = {
            //   'key'                  : this.get_object_key(file.name),
            //   'policy'               : this.sign_info.policy,        //有过期时间签名，可以考虑加长时间
            //   'OSSAccessKeyId'       : this.sign_info.accessid,
            //   'success_action_status': '200',                       //让服务端返回200,不然，默认会返回204
            //   'callback'             : this.sign_info.callback,
            //   'signature'            : this.sign_info.signature,
            // }
            //
            // up.setOption({
            //   'url'             : this.sign_info.host,
            //   'multipart_params': new_multipart_params
            // })

            console.log('exec => BeforeUpload...', new_multipart_params)

          },

          UploadProgress: (up, file) => {
//            console.log(`exec => UploadProgress() ...`, file.percent, file.name)
//            NProgress.set(this.uploader.total.percent * 0.01)
//            nanobar.go(this.uploader.total.percent)
          },

          FileUploaded: (up, file, info) => {

            let res     = JSON.parse(info.response),
                //                status  = res.status,     //info.status 实则为返回的真实状态。 200 成功； 203 上传到OSS成功，但是oss访问用户设置的上传回调服务器失败；其它status代码……
                //                message = res.message,
                file_id = `${up.settings.url}/${res.response.filename}`     //up.settings.url 即 OSS post's host url

//            console.log('exec => FileUploaded()', this, up, file_id)
            this.completed_files.push({file_id})

//            let r = {
//              response       : `{"status":200,"message":"回调成功!","response":{"filename":"webuploader-dir/2017/7/6/Screenshot_2015-09-16-16-50-55.png","size":"6137","mimeType":"image/png","height":"1920","width":"1080"}}`,
//              responseHeaders: 'Content-Type: application/json',
//              status         : 200
//            }

          },

          UploadComplete: (up, files) => {
            console.log(`all files UploadComplete `, this)
            this.percents.push(up.total.percent)

          },

          Error: function (up, err) {
            console.log(`Upload Error ...`, up, err)  // err: {code, file, message}
          }
        }
      })

      this.uploader.init()

//      console.log('mounded', $vm)

    }
  }


</script>
<style lang="less" scoped>

  .image-up-cover {
    width: 100vw;
    height: calc(100vw / 16 * 9);
  }

  .image-up-list {
    display: flex;
    flex-wrap: wrap;
    margin: 0.2rem 0 0 0.5rem;

    .item {
      position: relative;
      width: calc(33.3vw - 1.2rem);
      height: calc(33.3vw - 1.2rem);
      border: solid 1px burlywood;
    }
  }

  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s
  }

  .fade-enter, .fade-leave-to /* .fade-leave-active in below version 2.1.8 */ {
    opacity: 0
  }

</style>
