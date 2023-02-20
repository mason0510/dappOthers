<template>
	<div id="commentList">
		<Modal v-model="modal.value" title="评论列表" class="big-model">
			<div v-for="(item,index) in commentlist" :key="index" class="content">
				<p>用户名：{{item.userName}}</p>
				<p>评论时间：{{item.createTime | timeFormat(item.createTime)}}</p>
				<p>评论内容：{{item.content}}</p>
			</div>
			<Page :total="total" size="small" class-name="f-r mt16" @on-change="pageChange" @on-page-size-change="sizeChaneg" show-elevator show-sizer :current="current" :page-size="pageSize"></Page>
			<div slot="footer">
				<Button type="primary" @click="modal.value=false">关闭</Button>
			</div>
		</Modal>
	</div>
</template>

<script>
	import { getCommentList } from '../../api/comment';
	export default {
		data() {
			return {
				total: 40,
				current: 1,
				pageSize: 20,
				commentlist: []
			}
		},
		props: {
			modal: {
				type: Object,
				default: function() {
					return {
						value: false
					}
				}
			},
			refId: {
				type: [String, Number]
			},
			refType: {
				type: [String, Number]
			}
		},
		methods: {
			pageChange(val) {
				this.current = val;
				this.getList();
			},
			sizeChaneg(val) {
				this.pageSize = val;
				this.getList();
			},
			getList() {
				let _this = this;
				getCommentList({
					pageIndex: _this.current,
					refId: _this.refId,
					refType: _this.refType,
					pageSize: this.pageSize
				}).then(function(res) {
					if(res.data.data) {
						_this.commentlist = res.data.data
						_this.total=res.data.totalResults
						_this.current=res.data.currentPage
						_this.pageSize=res.data.pageSize
					} else {
						_this.commentlist = [
						]
					}
				})
			}
		},
		watch: {
			refId() {
				this.getList();
			}
		}
	}
</script>

<style lang="less" scoped="scoped">
	.content {
		padding-bottom: 10px;
		padding-top: 10px;
		border-bottom: 1px solid #E9E9E9;
		p {
			line-height: 26px;
		}
	}
</style>