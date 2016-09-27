import groovy.transform.BaseScript
import com.electriccloud.commander.dsl.util.BasePlugin

//noinspection GroovyUnusedAssignment
@BaseScript BasePlugin baseScript

// Variables available for use in DSL code
def pluginName = args.pluginName
def pluginKey = getProject("/plugins/$pluginName/project").pluginKey
def pluginDir = getProperty("/server/settings/pluginsDirectory").value + "/" + pluginName

project pluginName, {

	description = 'A set of procedures to interact with Electric Cloud support.'
	ec_visibility = 'all'

	property 'version', {
		value = '1.4.1'
		Description= new File(pluginDir + "/dsl/properties/CHANGELOG.txt").text
	}

	loadProcedures(pluginDir, pluginKey, pluginName)
}
