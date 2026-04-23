export default {
    data() {
        return {
            list: [], // 列表
            loading: false,
            finished: false,
            isLoading: false,
            form: { // 传递后端的参数
                page_no: 1, // 分页
                // start: dayjs().subtract(7, 'day').format('YYYY-MM-DD'), // 起始时间
                // end: dayjs().format('YYYY-MM-DD') // 终点时间
            }
        }
    },
    methods: {
        handleData(data) { // 往列表里拼接数据
            this.isLoading = false;
            this.list = this.list.concat(data)
            this.loading = false
            if (data.length < 20) { // 小于十条 说明就是最后一组数据
                this.finished = true
                console.log('没有更多数据了')
            }
            this.form.page_no++ // 翻页
        },
        onRefresh() { // 下拉刷新
            console.log('refresh')
            this.form.page_no = 1
            this.list = []
            this.get()
        },
        onLoad() {
            console.log('onLoad')
            this.get()
        },
    }
}
