package molinov.creditcalculator.view.creditslist

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import molinov.creditcalculator.R


class ItemTouchHelperCallback(
    private val adapter: CreditListAdapter,
    private val icon: Drawable,
    private val deleteBackground: GradientDrawable,
    private val editBackground: GradientDrawable
) : ItemTouchHelper.Callback() {

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
        adapter.onItemSwiped(viewHolder.adapterPosition, direction)
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView: View = viewHolder.itemView
            val iconMargin: Int = (itemView.height - icon.intrinsicHeight) / 2
            val iconTop: Int =
                itemView.top + (itemView.height - icon.intrinsicHeight) / 2
            val iconBottom: Int = iconTop + icon.intrinsicHeight
            val cardView = recyclerView.findViewById<MaterialCardView>(R.id.card)
            val width = itemView.width.toFloat()
            val alpha = 1.0f - kotlin.math.abs(dX) / width
            itemView.alpha = alpha
            itemView.translationX = dX
            when {
                dX < 0 -> { // Swiping to the left
                    val iconLeft: Int = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight: Int = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteBackground.setBounds(
                        itemView.left - cardView.paddingLeft,
                        itemView.top + cardView.paddingTop,
                        itemView.right - cardView.paddingRight,
                        itemView.bottom - cardView.paddingBottom
                    )
                    deleteBackground.cornerRadius = 10f
                    deleteBackground.draw(c)
                }
                dX > 0 -> {
                    val iconLeft: Int = itemView.left + iconMargin
                    val iconRight: Int = itemView.left + iconMargin + icon.intrinsicWidth
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    editBackground.setBounds(
                        itemView.left + cardView.paddingLeft,
                        itemView.top + cardView.paddingTop,
                        itemView.right + cardView.paddingRight,
                        itemView.bottom - cardView.paddingBottom
                    )
                    editBackground.cornerRadius = 10f
                    editBackground.draw(c)
                }
                else -> { // view is unSwiped
                    deleteBackground.setBounds(0, 0, 0, 0)
                    editBackground.setBounds(0, 0, 0, 0)
                }
            }
            icon.draw(c)
        }
    }
}
