
## ScaleView 
> 刻度尺组件,可自主圆形表盘效果

### 主要功能介绍
最开始是想做一横向的刻度尺效果,后来实现的时候发现竖向以及圆形刻度尺(表盘效果)实现代码有很大的相同之处,索性全部做出效果.

- [x] 实现横向,竖向,圆形刻度效果
- [x] 游标可自由设置图片,大小以及与刻度的距离
- [x] 可自定义刻度默认颜色和进度颜色
- [x] 刻度线宽度使用scaleNodeWidth,scaleWidth(占用整个组件大小比例)已经padding来控制
- [x] 游标图片不设置,就不会绘制游标,从而达到控制游标时候显示的效果
- [x] 刻度Text可不设置


### 效果图
更多效果运行demo查看
![device-2021-04-16-152444](https://user-images.githubusercontent.com/10650866/115175361-6bc25500-a0fd-11eb-9279-f42aa19f4c6f.png)


## Attributes属性
>在ScaleView布局文件中调用
下面的属性并不是每个指示器都用得到，所以使用时要注意！

|Attributes|format|describe
|---|---|---|
|scaleWidth|float|刻度线的宽度占用组件大小的比例
|scaleNodeWidth|float|节点刻度线宽度占用组件大小的比例
|scaleLineWidth|float|刻度线的线条粗细
|scaleDirect|enum|线性组件方向：horizontal（默认） or vertical 
|progressDirect|enum|暂无效果,后续逐渐完善
|scaleStyle|enum|组件样式 线性:line(默认) 圆形:circle
|cursorDrawable|reference|游标图片
|cursorSeat|enum|游标相对组件的位置 线性使用:left,top,right,bottom 圆形使用:inside,outside
|cursorWidth|dimension|游标是按照一个正方形来绘制的,指定游标宽度,相当于同时指定游标大小
|cursorGap|dimension|游标距离刻度线的距离
|scaleTextSeat|enum|刻度Text对组件的位置 线性使用:left,top,right,bottom 圆形使用:inside,outside
|scaleTextColor|color|刻度Text字体颜色
|scaleTextSize|dimension|刻度Text字体大小
|scaleTextGap|dimension|刻度Text距离刻度线的距离
|totalProgress|integer|刻度总进度
|unitScale|integer|刻度单元格
|defaultColor|color|刻度线默认颜色
|progressColor|color|刻度线进度颜色


## 使用示例

#### 示例 1.横向刻度

```xml
    <com.mrr.libscaleview.ScaleView
        android:id="@+id/horizontalScaleView"
        android:layout_width="320dp"
        android:layout_height="70dp"
        android:paddingLeft="20dp"
        android:paddingTop="25dp"
        android:paddingRight="20dp"
        android:paddingBottom="20dp"
        app:cursorDrawable="@drawable/night_brightness"
        app:cursorGap="0dp"
        app:cursorSeat="bottom"
        app:cursorWidth="10dp"
        app:defaultColor="@color/dark"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:progressColor="@color/purple_200"
        app:scaleDirect="horizontal"
        app:scaleStyle="line"
        app:scaleTextColor="@color/purple_200"
        app:scaleTextGap="2dp"
        app:scaleTextSeat="top"
        app:scaleTextSize="5sp" />
```

#### 示例 2.竖向刻度

```xml
    <com.mrr.libscaleview.ScaleView
        android:id="@+id/verticalScaleView"
        android:layout_width="70dp"
        android:layout_height="420dp"
        android:paddingLeft="20dp"
        android:paddingTop="20dp"
        android:paddingRight="25dp"
        android:paddingBottom="20dp"
        app:cursorDrawable="@drawable/night_brightness"
        app:cursorGap="0dp"
        app:cursorSeat="right"
        app:cursorWidth="10dp"
        app:defaultColor="@color/dark"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:progressColor="@color/purple_200"
        app:scaleDirect="vertical"
        app:scaleNodeWidth="0.7"
        app:scaleStyle="line"
        app:scaleTextColor="@color/purple_200"
        app:scaleTextGap="2dp"
        app:scaleTextSeat="left"
        app:scaleTextSize="5sp" />
```

#### 示例 2.圆形刻度

```xml
      <com.mrr.libscaleview.ScaleView
        android:id="@+id/circleScaleView"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:padding="20dp"
        app:cursorDrawable="@drawable/lollipop"
        app:cursorGap="3dp"
        app:cursorSeat="inside"
        app:cursorWidth="10dp"
        app:defaultColor="@color/dark"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.2"
        app:progressColor="@color/purple_200"
        app:scaleNodeWidth="0.2"
        app:scaleStyle="circle"
        app:scaleTextColor="@color/purple_200"
        app:scaleTextGap="2dp"
        app:scaleTextSeat="outside"
        app:scaleTextSize="5sp"
        app:scaleWidth="0.1"
        app:totalProgress="120"
        app:unitScale="10" />
```
