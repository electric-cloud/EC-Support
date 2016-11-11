def pluginName = args.pluginName
def pluginKey = getProject("/plugins/$pluginName/project").pluginKey
def pluginDir = getProperty("/server/settings/pluginsDirectory").value + "/" + pluginName

deleteProperty propertyName: "/server/ec_customEditors/pickerStep/$pluginKey - Sample Procedure"

return "Demoting plugin"
