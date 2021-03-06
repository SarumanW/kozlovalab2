package dao.json_dao;

import caching.SingletonCache;
import dao.dao_interface.SprintDAO;
import domain.Sprint;
import generator.JsonParser;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class JsonSprintDAO implements SprintDAO {
    private static final String FILE_NAME = "F:\\save\\netcracker\\kozlovalab2\\src\\main\\resources\\json\\json-sprint.txt";

    private Sprint parseJson(JSONObject jsonObject){
        Sprint sprint = new Sprint();

        sprint.setSprintID(jsonObject.getLong("sprintID"));
        sprint.setName(jsonObject.getString("name"));
        sprint.setProjectID(jsonObject.getLong("projectID"));
        List<Long> taskList= new ArrayList<>();
        for(Object taskId : jsonObject.getJSONArray("taskList")){
            taskList.add(Long.parseLong(taskId.toString()));
        }
        sprint.setTaskList(taskList);

        return sprint;
    }

    @Override
    public boolean insertSprint(Sprint sprint) {
        String stringJson = new JSONObject(sprint).toString();

        try(FileWriter fw = new FileWriter(FILE_NAME, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.write(stringJson);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Sprint findSprint(long key) {
        Sprint sprint = (Sprint) SingletonCache.getInstance().get(key);

        if(sprint != null)
            return sprint;

        sprint = parseJson(JsonParser.parseFile
                (key, Sprint.class, FILE_NAME));
        SingletonCache.getInstance().put(key, sprint);

        return sprint;
    }

    @Override
    public boolean updateSprint(Sprint sprint) {
        SingletonCache.getInstance().put(sprint.getSprintID(), sprint);
        deleteSprint(sprint.getSprintID());
        insertSprint(sprint);
        return true;
    }

    @Override
    public boolean deleteSprint(long key) {
        JsonParser.removeLineFromFile(FILE_NAME,
                JsonParser.parseFile(key, Sprint.class, FILE_NAME).toString());
        SingletonCache.getInstance().remove(key);
        return true;
    }
}
