package engine;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import engine.animation.AnimatedActor;
import engine.game.GameWolfen;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInsideOutCubeColor;

@SuppressWarnings("unused")
public class Map implements Displayable{

	private EntityActor sky;
	public DisplayableArray2D list;
	public int x, y;

	public GameWolfen game; 

	public Map(GameWolfen game) {
		list = new DisplayableArray2D(0, 0);

		this.game = game;

		sky = new EntityActor(new ShapeInsideOutCubeColor(game.shaderProgramSky));

		x = y = 20;

		//generate();
		//fullRandom();
	}

	public void setSize(int x, int y){
		this.x = x;
		this.y = y;

		list = new DisplayableArray2D(x, y);
	}

	@Override
	public void update(long dt) {
		list.update(dt);

		int x = (int) (game.camera.position.x + 0.5f);
		int z = (int) (game.camera.position.z + 0.5f);

		for(int i = x - 1; i < x + 2; i++){
			for(int j = z - 1; j < z + 2; j++){

				Displayable d = list.get(i, j);

				if (!(d instanceof Entity))
					continue;

				Entity e = (Entity) d;

				if(!e.isSolid())
					continue;

				if(game.camera.collide(e)){
					game.camera.collisionHandler(e);
				}
			}
		}
	}

	@Override
	public void render(Camera camera) {
		sky.render(camera);
		list.render(camera);
	}
	/*
	public void generate(){

		list = new DisplayableArray2D(x, y);

		setSky();

		for(int i = 0; i < x; i++){
			for(int j = 0; j < y; j++){
				//char c = map.charAt((i * x) + j);
				char c = 'O';
				if(c == 'O'){
					newWall(x - i - 1, j, game.shapeOrdinaryWall, true);
				}
				if(c == 'X'){
					newActor(x - i - 1, j, game.shapePillar, true);
				}
			}
		}

		setOrientation();
	}
	 */
	public void newActor(float x, float y, Shape shape, boolean solid){
		x = this.x - x - 1;
		EntityActor e = new EntityActor(shape);
		e.position = new Vector3f(x, 0, y);
		e.setSolid(solid);

		list.add(e, (int)x, (int)y);
	}
	
	public void newAnimatedActor(float x, float y, Shape shape, boolean solid){
		x = this.x - x - 1;
		AnimatedActor e = new AnimatedActor(shape, 512, 128);
		e.position = new Vector3f(x, 0, y);
		e.setSolid(solid);

		list.add(e, (int)x, (int)y);
	}

	public void newWall(float x, float y, ShapeCubeTexture shape, boolean solid){
		x = this.x - x - 1;
		EntityWall e = new EntityWall(shape);
		e.position = new Vector3f(x, 0, y);
		e.setSolid(solid);

		list.add(e, (int)x, (int)y);
	}
	/*
	public void fullRandom(){

		setSky();

		list = new DisplayableArray2D(x, y);

		for(int i = 0; i < x; i++){
			for(int j = 0; j < y; j++){

				if(i == 0 || i == x-1 || j == 0 || j == y-1){
					newWall(i, j, game.shapeOrdinaryWall, true);
					continue;
				}

				int rand = new Random().nextInt(100);

				if(rand < 25){
					newWall(i, j, game.shapeOrdinaryWall, true);
				}
				else if(rand < 30){
					newActor(i, j, game.shapePillar, true);
				}
			}
		}

		setOrientation();
	}
	 */
	public void setOrientation() {
		for(int i = 0; i < x; i++){
			for(int j = 0; j < y; j++){

				EntityActor actor = (EntityActor) list.get(i, j);

				if(actor == null){
					continue;
				}

				if (!(actor instanceof EntityWall))
					continue;

				StringBuilder stb = new StringBuilder();


				if(i > 0)
					processOrientation(stb, actor, i-1, j, 'w');

				if(j > 0)
					processOrientation(stb, actor, i, j-1, 's');

				if(i < x - 1)
					processOrientation(stb, actor, i+1, j, 'e');

				if(j < y - 1)
					processOrientation(stb, actor, i, j+1, 'n');

				if (actor instanceof EntityWall) {
					EntityWall new_name = (EntityWall) actor;
					new_name.setOrientation(stb.toString());
				}
			}
		}
	}

	private void processOrientation(StringBuilder stb, EntityActor actor, int i, int j, char c){
		Displayable d = list.get(i, j);

		if(!(d instanceof EntityWall)){
			stb.append(c);
			return;
		}

		EntityWall ew = (EntityWall) d;
		
		if(!actor.isSolid() && !ew.isSolid())
			return;
		
		if(!ew.isSolid())
			stb.append(c);

	}

	public void setSky(){
		((ShapeInsideOutCubeColor) sky.shape).scale = new Vector3f(x, 1, y);
		sky.position = new Vector3f(x / 2, 0, y / 2);
	}
}
