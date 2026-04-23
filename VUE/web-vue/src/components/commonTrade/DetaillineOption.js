import echarts from 'echarts'
var lineOption = {
	color: ['#007aff'],
	title: {
		text: ''
	},
	axisPointer: {
		link: {
			xAxisIndex: 'all'
		},
		label: {
			backgroundColor: '#777'
		}
	},
	tooltip: {
		trigger: 'axis',
		axisPointer: {
			type: 'cross'
		},
		backgroundColor: 'rgba(245, 245, 245, 0.8)',
		borderWidth: 1,
		borderColor: '#ccc',
		padding: 10,
		textStyle: {
			color: '#000'
		},
		position: function (pos, params, el, elRect, size) {
			var obj = {top: 10};
			obj[['left', 'right'][+(pos[0] < size.viewSize[0] / 2)]] = 30;
			return obj;
		}
		// extraCssText: 'width: 170px'
	},
	grid: [
		{
			left: '8%',
			right: '3%',
			height: '68%',
			top: '3%'
		},
		{
			left: '8%',
			right: '3%',
			top: '74%',
			height: '16%'
		}
	],
	dataZoom: [{
			type: 'inside',
			xAxisIndex: [0, 1],
			start: 75,
			end: 100
		},
		{
			show: false,
			xAxisIndex: [0, 1],
			type: 'slider',
			top: '85%',
			start: 75,
			end: 100
		}
	],
	legend: {
		show: false
	},
	xAxis: [{
		type: 'category',
		scale: true,
		boundaryGap: false,
		data: [],
		axisLine: {
			show: false,
			lineStyle: {
				color: '#8392A5'
			}
		},
		axisTick: {
			show: false
		},
		splitLine: {
			show: false
		},
		splitNumber: 20,
		offset: 55,
		axisLabel: {
			color: '#6B7B92',
		},
		splitLine: {
			show: false,
			lineStyle: {
				color: ['#2A3646']
			}
		},
		axisPointer: {
			z: 100
		}
	}, {
		type: 'category',
		gridIndex: 1,
		scale: true,
		boundaryGap: false,
		data: [],
		axisTick: {
			show: false
		},
		splitLine: {
			show: false
		},
		axisLabel: {
			show: false
		},
		splitNumber: 20,
	}],
	yAxis: [{
		// position:'right',
		// offset: 50,
		min: 0,
		scale: true,
		splitLine: {
			show: false,
			lineStyle: {
				color: ['#2A3646']
			}
		},
		axisLine: {
			show: false,
			lineStyle: {
				color: '#8392A5'
			}
		},
		axisTick: {
			show: false
		},
		axisLabel: {
			// inside: true,
			color: '#6B7B92',
			formatter: function(val) {
				if (val > 100000) {
					return (val / 10000).toFixed(0) + 'w'
				} else {
					return val
				}
			}
		}
	}, {
		// position:'right',
		// offset: 50,
		scale: true,
		gridIndex: 1,
		splitNumber: 2,
		axisLabel: {
			show: false
		},
		axisLine: {
			show: false,
			lineStyle: {
				color: '#8392A5'
			}
		},
		axisTick: {
			show: false
		},
		splitLine: {
			show: false
		}
	}],
	series: [{
		name: 'Close',
		symbol: "none",
		type: 'line',
		markPoint: {
				symbol: 'circle',
				symbolSize:7,
				label: {
						show:true,
						position: 'top',
				},
				data: [
						{
								name: '',
								coord: [],
								value: 0,
								itemStyle: {
										color: 'rgb(255,255,255)'
								}
						}
				]
		},
		areaStyle: {
			// normal: {
			// 	color: 'rgba(30, 64, 103, 0.2)' //改变区域颜色
			// },
			normal: {   //颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
				color: new echarts.graphic.LinearGradient(0, 0, 0, 1,[{
								offset: 0, color: 'rgba(30, 64, 103, 0.8)' // 0% 处的颜色
						}, {
								offset: 0.5, color: 'rgba(30, 64, 103, 0.4)' // 100% 处的颜色
						}, {
								offset: 1, color: 'rgba(30, 64, 103, 0)' // 100% 处的颜色
						}]
				),  //背景渐变色
			}, 
		},
		lineStyle: {
			normal: {
				width: 1
			}
		},
		smooth: true,
		data: []
	}, {
		name: 'Volume',
		type: 'bar',
		xAxisIndex: 1,
		yAxisIndex: 1,
		data: [],
		barWidth: '5%',
	}]
}


export default lineOption;
