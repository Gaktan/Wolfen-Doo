package engine.game;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.DisplayableArray2D;
import engine.entities.AnimatedActor;
import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.EntityActor;
import engine.entities.EntityWall;
import engine.shapes.*;

public class Map implements Displayable{

	public EntityActor sky;
	public DisplayableArray2D list;
	public int x, y;
	private boolean delete;

	public GameWolfen game;

	public Map(GameWolfen game) {
		this(game, 20, 20, null);
	}

	public Map(GameWolfen game, int x, int y, EntityActor sky){
		list = new DisplayableArray2D(x, y);

		this.game = game;

		if(sky == null){
			ShapeInsideOutCubeColor skyShape = new ShapeInsideOutCubeColor(game.shaderProgramSky);
			
			skyShape.downColor = new Vector3f(0.75f, 0.75f, 0.75f);
			skyShape.upColor = new Vector3f(0.35f, 0.75f, 0.9f);
			
			sky = new EntityActor(skyShape);
		}

		this.x = x;
		this.y = y;

	}

	public void setSize(int x, int y){
		this.x = x;
		this.y = y;

		list = new DisplayableArray2D(x, y);
	}

	@Override
	public boolean update(float dt) {

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
		
		boolean b = list.update(dt);
		
		if(!b)
			return false;
		
		return !delete;
	}

	@Override
	public void render(Camera camera) {
		
		sky.render(camera);
		list.render(camera);
	}

	public void buildMapFromString(String st){

		list = new DisplayableArray2D(x, y);

		setSky();

		ShapeCubeTexture shapeCube = new ShapeCubeTexture(game.shaderProgramTex, "wall.png");
		//ShapeQuadTexture shapeCube = new ShapeQuadTexture(game.shaderProgramTexBill, "wall.png");

		for(int i = 0; i < x; i++){
			for(int j = 0; j < y; j++){
				char c = st.charAt((i * y) + j);
				if(c == 'O'){
					newWall(x - i - 1, j, shapeCube, true);
					//newActor(x - i - 1, j, shapeCube, false);
				}
			}
		}

		setOrientation();
	}

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

	@Override
	public void delete() {
		delete = true;
	}
}
