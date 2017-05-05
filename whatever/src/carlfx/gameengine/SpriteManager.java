package carlfx.gameengine;

import java.util.*;
public class SpriteManager {
    private final static List<Sprite> GAME_ACTORS = new ArrayList<>();
    private final static List<Sprite> CHECK_COLLISION_LIST = new ArrayList<>();
    private final static Set<Sprite> CLEAN_UP_SPRITES = new HashSet<>();
    public List<Sprite> getAllSprites() {
        return GAME_ACTORS;
    }
    public void addSprites(Sprite... sprites) {       
        GAME_ACTORS.addAll(Arrays.asList(sprites));
    }
    public void removeSprites(Sprite... sprites) {       
        GAME_ACTORS.removeAll(Arrays.asList(sprites));
    }
 
    public Set<Sprite> getSpritesToBeRemoved() {
        return CLEAN_UP_SPRITES;
    }
    public void addSpritesToBeRemoved(Sprite... sprites) {
        if (sprites.length > 1) {
            CLEAN_UP_SPRITES.addAll(Arrays.asList((Sprite[]) sprites));
        } else {
            CLEAN_UP_SPRITES.add(sprites[0]);
        }
    }

    public List<Sprite> getCollisionsToCheck() {
        return CHECK_COLLISION_LIST;
    }
    public void resetCollisionsToCheck() {
        CHECK_COLLISION_LIST.clear();
        CHECK_COLLISION_LIST.addAll(GAME_ACTORS);
    }
    public void cleanupSprites() {
        GAME_ACTORS.removeAll(CLEAN_UP_SPRITES);
        CLEAN_UP_SPRITES.clear();
    }
}
