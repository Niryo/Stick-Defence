package huji.ac.il.stick_defence;

import android.content.Context;
import java.util.ArrayList;

/**
 * This class represents the game state of stick-defence game.
 * That is contains all sprites, handle them and manage
 * the interactions between them.
 */
public class GameState {
    private ArrayList<Sprite> m_sprites;

    /**
     * Constructor. Adds 2 towers to the sprites list.
     * @param context the context
     */
    public GameState(Context context){
        m_sprites = new ArrayList<>();
        m_sprites.add(new Tower(context, Tower.Player.RIGHT));
        m_sprites.add(new Tower(context, Tower.Player.LEFT));

   //     m_sprites.add(new BasicSoldier(context, BasicSoldier.Player.LEFT));

    }

    /**
     * Update the place and pictures of the sprites, but doesn't print them.
     */
    public void update(){
        for (Sprite sprite : m_sprites){
            sprite.update(System.currentTimeMillis());
        }
    }

    /**
     * adds a soldier to the requested player
     * @param context the context
     * @param player the requested player
     */
    public void addSoldier(Context context, BasicSoldier.Player player){
        m_sprites.add(new BasicSoldier(context, player));
    }

    /**
     * Returns the sprite list
     * @return the sprite list
     */
    public ArrayList<Sprite> getSpriteList(){
        return this.m_sprites;
    }
}
