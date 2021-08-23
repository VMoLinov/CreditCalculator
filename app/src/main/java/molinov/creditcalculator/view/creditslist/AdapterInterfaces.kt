package molinov.creditcalculator.view.creditslist

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemSwiped(position: Int, direction: Int)
}

interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}
