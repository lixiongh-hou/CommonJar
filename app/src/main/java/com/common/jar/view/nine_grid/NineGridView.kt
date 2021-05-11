package com.common.jar.view.nine_grid

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.common.jar.R
import com.common.tool.util.dpToPx


/**
 * 填充模式，类似于微信
 */
const val MODE_FILL = 0

/**
 * 网格模式，类似于QQ，4张图会 2X2布局
 */
const val MODE_GRID = 1


/**
 * @author 李雄厚
 *
 * @features 九宫视图
 */
class NineGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    /**
     * 单张图片时的最大大小,单位dp
     */
    private var singleImageSize = 250.dpToPx

    /**
     * 单张图片的宽高比(宽/高)
     */
    private var singleImageRatio = 1.0F

    /**
     * 最大显示的图片数
     */
    private var maxImageSize = 9

    /**
     * 宫格间距，单位dp
     */
    private var gridSpacing = 3.dpToPx

    /**
     * 默认使用fill模式
     */
    private var mode = MODE_FILL

    /**
     * 列数
     */
    private var columnCount = 0

    /**
     * 行数
     */
    private var rowCount = 0

    /**
     * 宫格宽度
     */
    private var gridWidth = 0

    /**
     * 宫格高度
     */
    private var gridHeight = 0

    private var imageViews = mutableListOf<AppCompatImageView>()
    private var mImageInfo: MutableList<ImageInfo>? = null
    private lateinit var mAdapter: NineGridViewAdapter

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NineGridView).apply {
            gridSpacing =
                getDimension(
                    R.styleable.NineGridView_ngv_gridSpacing,
                    gridSpacing.toFloat()
                ).toInt()
            singleImageSize = getDimensionPixelSize(
                R.styleable.NineGridView_ngv_singleImageSize,
                singleImageSize
            )
            singleImageRatio =
                getFloat(R.styleable.NineGridView_ngv_singleImageRatio, singleImageRatio)
            maxImageSize = getInt(R.styleable.NineGridView_ngv_maxSize, maxImageSize)
            mode = getInt(R.styleable.NineGridView_ngv_mode, mode)
        }
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0
        val totalWidth = width - paddingLeft - paddingRight
        mImageInfo?.let {
            if (it.isNotEmpty()) {
                if (it.size == 1) {
                    gridWidth = if (singleImageSize > totalWidth) totalWidth else singleImageSize
                    gridHeight = (gridWidth / singleImageRatio).toInt()
                    //矫正图片显示区域大小，不允许超过最大显示范围
                    if (gridHeight > singleImageSize) {
                        val ratio = singleImageSize * 1.0f / gridHeight
                        gridWidth = (gridWidth * ratio).toInt()
                        gridHeight = singleImageSize
                    }
                } else {
                    //这里无论是几张图片，宽高都按总宽度的 1/3
                    gridWidth = (totalWidth - gridSpacing * 2) / 3
                    gridHeight = (totalWidth - gridSpacing * 2) / 3
                }
                width =
                    gridWidth * columnCount + gridSpacing * (columnCount - 1) + paddingLeft + paddingRight
                height =
                    gridHeight * rowCount + gridSpacing * (rowCount - 1) + paddingTop + paddingBottom
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mImageInfo?.let {
            it.forEachIndexed { index, _ ->
                val childrenView = getChildAt(index) as AppCompatImageView
                val rowNum = index / columnCount
                val columnNum = index % columnCount
                val left = (gridWidth + gridSpacing) * columnNum + paddingLeft
                val top = (gridHeight + gridSpacing) * rowNum + paddingTop
                val right = left + gridWidth
                val bottom = top + gridHeight
                childrenView.layout(left, top, right, bottom)
                mImageLoader?.onDisplayImage(context, childrenView, it[index].thumbnailUrl)
            }
        }


    }

    fun setAdapter(adapter: NineGridViewAdapter) {
        mAdapter = adapter
        var imageInfo: MutableList<ImageInfo> = adapter.imageInfo
        if (imageInfo.isNullOrEmpty()) {
            visibility = GONE
            return
        } else {
            visibility = VISIBLE
        }
        var imageCount = imageInfo.size
        if (maxImageSize in 1 until imageCount) {
            imageInfo = imageInfo.subList(0, maxImageSize)
            //再次获取图片数量
            imageCount = imageInfo.size
        }
        //默认是3列显示，行数根据图片的数量决定
        rowCount = imageCount / 3 + if (imageCount % 3 == 0) 0 else 1
        columnCount = 3
        //grid模式下，显示4张使用2X2模式
        if (mode == MODE_GRID) {
            if (imageCount == 4) {
                rowCount = 2
                columnCount = 2
            }
        }
        //保证View的复用，避免重复创建
        if (mImageInfo == null) {
            for (i in 0 until imageCount) {
                val iv: AppCompatImageView = getImageView(i) ?: return
                addView(iv, generateDefaultLayoutParams())
            }
        } else {
            val oldViewCount = mImageInfo!!.size
            val newViewCount = imageCount
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount, oldViewCount - newViewCount)
            } else if (oldViewCount < newViewCount) {
                for (i in oldViewCount until newViewCount) {
                    val iv: ImageView = getImageView(i) ?: return
                    addView(iv, generateDefaultLayoutParams())
                }
            }
        }
        //修改最后一个条目，决定是否显示更多
        if (adapter.imageInfo.size > maxImageSize) {
            val child = getChildAt(maxImageSize - 1)
//            if (child is NineGridViewWrapper) {
//                child.moreNum = adapter.imageInfo.size - maxImageSize
//            }
        }
        mImageInfo = imageInfo
        requestLayout()
    }

    private fun getImageView(position: Int): AppCompatImageView {
        val imageView: AppCompatImageView
        if (position < imageViews.size) {
            imageView = imageViews[position]
        } else {
            imageView = mAdapter.generateImageView(context)
            imageView.setOnClickListener {
                mAdapter.onImageItemClick(
                    context, this@NineGridView, position, mAdapter.imageInfo
                )
            }
            imageViews.add(imageView)
        }
        return imageView
    }

    /** 设置宫格间距  */
    fun setGridSpacing(spacing: Int) {
        gridSpacing = spacing
    }

    /** 设置只有一张图片时的宽  */
    fun setSingleImageSize(maxImageSize: Int) {
        singleImageSize = maxImageSize
    }

    /** 设置只有一张图片时的宽高比  */
    fun setSingleImageRatio(ratio: Float) {
        singleImageRatio = ratio
    }

    /** 设置最大图片数  */
    fun setMaxSize(maxSize: Int) {
        maxImageSize = maxSize
    }

    fun getMaxSize(): Int {
        return maxImageSize
    }

    companion object {
        /**
         * 全局的图片加载器(必须设置,否者不显示图片)
         */
        private var mImageLoader: ImageLoader? = null
        fun setImageLoader(imageLoader: ImageLoader) {
            mImageLoader = imageLoader
        }

        fun getImageLoader(): ImageLoader? {
            return mImageLoader
        }
    }

    interface ImageLoader {
        /**
         * 需要子类实现该方法，以确定如何加载和显示图片
         *
         * @param context   上下文
         * @param imageView 需要展示图片的ImageView
         * @param url       图片地址
         */
        fun onDisplayImage(context: Context, imageView: AppCompatImageView, url: String)

        /**
         * @param url 图片的地址
         * @return 当前框架的本地缓存图片
         */
        fun getCacheImage(url: String): Bitmap?
    }
}