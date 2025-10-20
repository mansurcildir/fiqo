import { useEffect, useState } from 'react';
import { fileAPI } from '../../service/file-service';
import { FileInfo } from '../../model/file/FileInfo';
import { FileManager } from '@cubone/react-file-manager';
import '@cubone/react-file-manager/dist/style.css';
import PageMeta from '../../components/common/PageMeta';
import { useAlert } from '../../service/alert-service';
import { SPRING_BASE_URL } from '../../utils/utils';
import { getAccessToken } from '../../service/storage-manager';

interface FileItem {
  name: string;
  isDirectory: boolean;
  path: string;
  size: number;
  updatedAt: string;
}

export default function Home() {
  const [files, setDirectories] = useState<FileItem[]>([]);
  const [currentPath] = useState<string>('');
  const { showAlert } = useAlert();

  useEffect(() => {
    fetchFiles();
  }, [currentPath]);

  const fetchFiles = async () => {
    fileAPI
      .getAllFiles(currentPath)
      .then((res) => {
        seperateDirs(res.data);
      })
      .catch((err) => {
        showAlert(err.response.data.message, 'error');
      });
  };

  const seperateDirs = (files: FileInfo[]) => {
    const dirs: FileItem[] = [];

    files.forEach((file) => {
      const parts = file.path.split('/');

      parts.forEach((part, index) => {
        const path = parts.slice(0, index + 1).join('/');

        if (!dirs.find((d) => d.path === path)) {
          dirs.push({
            name: part,
            path: path,
            isDirectory: index + 1 === parts.length ? false : true,
            size: index + 1 === parts.length ? file.size : 0,
            updatedAt: file.updatedAt
          });
        }
      });
    });

    setDirectories(dirs);
  };

  const deleteFile = (path: string, recursive: boolean) => {
    fileAPI
      .deleteFile(path, recursive)
      .then(() => {
        fetchFiles();
      })
      .catch((err) => {
        showAlert(err.response.data.message, 'error');
      });
  };

  const downloadFile = (path: string, fileName: string) => {
    fileAPI
      .downloadFile(path)
      .then((blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.click();
        URL.revokeObjectURL(url);
        fetchFiles();
      })
      .catch((err) => {
        showAlert(err.response.data.message, 'error');
      });
  };

  const CustomPreview: React.FC<{ file: FileItem }> = ({ file }) => {
    const [url, setUrl] = useState<string>();

    useEffect(() => {
      const fetchPreview = async () => {
        fileAPI
          .downloadFile(`${file.path}`)
          .then((blob) => {
            setUrl(URL.createObjectURL(blob));
          })
          .catch((err) => {
            showAlert(err.response.data.message, 'error');
          });
      };
      fetchPreview();
    }, [file]);

    if (!url) return <>Loading...</>;
    return <img className="w-full" src={url} alt={file.name} />;
  };

  return (
    <>
      <PageMeta title="fiqo | Home" description="Home Page" />

      <h3 className="py-4 text-base font-medium text-gray-800 dark:text-white/90">File Explorer</h3>

      <FileManager
        primaryColor={'#465fff'}
        files={files}
        filePreviewComponent={(file: FileItem) => <CustomPreview file={file} />}
        onDownload={(files: Array<FileItem>) => {
          files.forEach((file) => {
            downloadFile(file.path, file.name);
          });
        }}
        onDelete={(files: Array<FileItem>) => {
          files.forEach((file) => {
            deleteFile(file.path, false);
          });
        }}
        fileUploadConfig={{
          url: `${SPRING_BASE_URL}/v1/files/multipart`,
          method: 'PUT',
          headers: {
            Authorization: `Bearer ${getAccessToken()}`
          }
        }}
        onFileUploading={(file: FileItem, parentFolder: FileItem) => {
          const uploadPath = `${parentFolder?.path ? parentFolder?.path + '/' : ''}${file.name}`;
          return { path: uploadPath };
        }}
        onCreateFolder={(name: string, parentFolder: FileItem) => {
          const file: FileItem = {
            name: name,
            isDirectory: true,
            path: `${parentFolder?.path ? parentFolder.path + '/' : ''}${name}`,
            size: 0,
            updatedAt: new Date().toISOString()
          };

          setDirectories([file, ...files]);
        }}
      />
    </>
  );
}
