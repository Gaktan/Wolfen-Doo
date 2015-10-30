package engine.game;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.DisplayableArray2D;
import engine.animations.AnimatedActor;
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

		ShapeCubeTexture shapeCube = new ShapeCubeTexture(game.shaderProgramTex, "wall");
		//ShapeQuadTexture shapeCube = new ShapeQuadTexture(game.shaderProgramTexBill, "wall");

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
		AnimatedActor e = new AnimatedActor(shape, "test", "a_running_front");
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

				int orientation = 0;
				
				if(i > 0)
					orientation += processOrientation(actor, i-1, 	j, 		Orientation.WEST);

				if(j > 0)
					orientation += processOrientation(actor, i, 	j-1,	Orientation.SOUTH);

				if(i < x - 1)
					orientation += processOrientation(actor, i+1, 	j,		Orientation.EAST);

				if(j < y - 1)
					orientation += processOrientation(actor, i, 	j+1,	Orientation.NORTH);

				if (actor instanceof EntityWall) {
					EntityWall new_name = (EntityWall) actor;
					new_name.setOrientation(orientation);
				}
			}
		}
	}

	private int processOrientation(EntityActor actor, int i, int j, int o){
		Displayable d = list.get(i, j);

		if(!(d instanceof EntityWall)){
			return o;
		}

		EntityWall ew = (EntityWall) d;

		if(!actor.isSolid() && !ew.isSolid())
			return 0;

		if(!ew.isSolid())
			return o;
		
		return 0;

	}

	public void setSky(){
		((ShapeInsideOutCubeColor) sky.shape).scale = new Vector3f(x-0.5f, 1, y-0.5f);
		sky.position = new Vector3f((x-1f) / 2, 0, (y-1f) / 2);
	}

	@Override
	public void delete() {
		delete = true;
	}
	
	public int size()
	{
		return list.size() + 1;
	}
}
