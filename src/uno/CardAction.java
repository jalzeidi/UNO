/* CardAction.java
 * Represents a card's action
 * If a card is a number card or a regular wild card(non draw 4) then the action is NONE
 */

package uno;

public enum CardAction {
    DRAW2,
    DRAW4,
    SKIP,
    REVERSE,
    NONE
}