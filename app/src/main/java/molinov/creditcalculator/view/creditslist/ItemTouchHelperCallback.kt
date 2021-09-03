package molinov.creditcalculator.view.creditslist

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import molinov.creditcalculator.R


class ItemTouchHelperCallback(private val adapter: CreditListAdapter) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (!viewHolder.itemView.findViewById<LinearLayoutCompat>(R.id.description).isVisible) {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            makeMovementFlags(
                dragFlags,
                swipeFlags
            )
        } else makeMovementFlags(0, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemSwiped(viewHolder, direction)
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(
        recyclerView: RecyclerView, viewHolder:
        RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
        itemViewHolder.onItemClear()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        var background: GradientDrawable? = null
        val itemView: View = viewHolder.itemView
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val cardView = recyclerView.findViewById<MaterialCardView>(R.id.card)
            val width = itemView.width.toFloat()
            val alpha = 1.0f - kotlin.math.abs(dX) / width
            itemView.alpha = alpha
            itemView.translationX = dX
            when {
                dX < 0 -> {
                    val icon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_delete)!!
                    background = GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        intArrayOf(
                            itemView.resources.getColor(R.color.recycle_red_dark, null),
                            itemView.resources.getColor(R.color.recycle_red_light, null)
                        )
                    )
                    setIcon(icon, itemView, ItemTouchHelper.LEFT)
                    background.apply {
                        setBounds(
                            itemView.left - cardView.marginLeft,
                            itemView.top + cardView.marginTop,
                            itemView.right - cardView.marginRight,
                            itemView.bottom - cardView.marginBottom
                        )
                        cornerRadius = cardView.radius
                        draw(c)
                    }
                    icon.draw(c)
                }
                dX > 0 -> {
                    val icon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_edit)!!
                    background = GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        intArrayOf(
                            itemView.resources.getColor(R.color.recycle_orange_light, null),
                            itemView.resources.getColor(R.color.recycle_orange_dark, null)
                        )
                    )
                    setIcon(icon, itemView, ItemTouchHelper.RIGHT)
                    background.apply {
                        setBounds(
                            itemView.left + cardView.marginLeft,
                            itemView.top + cardView.marginTop,
                            itemView.right + cardView.marginRight,
                            itemView.bottom - cardView.marginBottom
                        )
                        cornerRadius = cardView.radius
                        draw(c)
                    }
                    icon.draw(c)
                }
                else -> background?.setBounds(0, 0, 0, 0)
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            val clippedDy =
                clip(recyclerView.height, itemView.top, itemView.bottom, dY) // Set bounds for drag
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                clippedDy,
                actionState,
                isCurrentlyActive
            )
        }
    }

    private fun setIcon(icon: Drawable, itemView: View, direction: Int) {
        val iconMargin: Int = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop: Int = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom: Int = iconTop + icon.intrinsicHeight
        when (direction) {
            ItemTouchHelper.LEFT -> {
                val iconLeft: Int = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight: Int = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
            ItemTouchHelper.RIGHT -> {
                val iconLeft: Int = itemView.left + iconMargin
                val iconRight: Int = itemView.left + iconMargin + icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
        }
    }

    private fun clip(height: Int, top: Int, bottom: Int, delta: Float): Float {
        val newTop = top + delta
        val newBottom = bottom + delta
        val oobTop = 0 - newTop
        val oobBottom = newBottom - height
        return when {
            oobTop > 0 -> delta + oobTop
            oobBottom > 0 -> delta - oobBottom
            else -> delta
        }
    }
}
